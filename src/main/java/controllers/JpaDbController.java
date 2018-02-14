package controllers;

import java.util.List;

import javax.sql.DataSource;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import entities.Block;
import entities.Transaction;
import repositories.BlockRepository;
import repositories.TransactionRepository;

@EnableScheduling
@Controller
public class JpaDbController {

	@Autowired
	DataSource ds;

	@Autowired
	BlockRepository blockRepo;

	@Autowired
	TransactionRepository txRepo;

	@Scheduled(fixedDelay = 60000)
	@RequestMapping(value = "/db/sync")
	@ResponseBody
	public String sync() {

		System.out.println("Syncing database with blockchain.");

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse blockResponse, chainResponse;
		int currentBlocks = 0;
		String blockToGet = "";
		/*
		 * get block height + latest block hash
		 */
		HttpGet httpChainInfo = new HttpGet("http://localhost:8332/rest/chaininfo.json");
		try {
			chainResponse = httpclient.execute(httpChainInfo);
			String chainInfo = EntityUtils.toString(chainResponse.getEntity());

			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree = mapper.readTree(chainInfo);
			currentBlocks = tree.get("blocks").asInt();
			blockToGet = tree.get("bestblockhash").asText();

		} catch (HttpHostConnectException e) {
			System.out.println("Could not connect to daemon : probably offline");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		int minBlockHeight = currentBlocks - 5; // for testing

		// does repo contain latest block hash
		List<Block> matchingBlocks = blockRepo.findByHash(blockToGet);
		boolean syncedAll = (matchingBlocks.isEmpty() ? false : true);

		while (!syncedAll) {
			try {

				// get next block
				HttpGet httpgetBlock = new HttpGet("http://localhost:8332/rest/block/" + blockToGet + ".json");
				blockResponse = httpclient.execute(httpgetBlock);
				String blockInfo = EntityUtils.toString(blockResponse.getEntity());

				if (blockInfo.contains("Verifying blocks")) {
					System.out.println("Bitcoind still syncing with network.");
				} else {

					ObjectMapper mapper = new ObjectMapper();
					Block generatedBlock = mapper.readValue(blockInfo, Block.class);

					// if not recent block
					if (generatedBlock.getHeight() < minBlockHeight) {
						System.out.println("Not one of the last 5 blocks");
						syncedAll = true;
						return "Synced all.";
					} else {
						// save block and its transactions

						System.out.println("Saving block with hash:" + generatedBlock.getHash());
						blockRepo.save(generatedBlock);

						String blockHash = generatedBlock.getHash();
						for (Transaction t : generatedBlock.getTransactions()) {
							t.setBlockHash(blockHash);
						}

						txRepo.save(generatedBlock.getTransactions());
					}

					blockToGet = generatedBlock.getPrevBlockHash();
					matchingBlocks = blockRepo.findByHash(blockToGet);

					syncedAll = (matchingBlocks.isEmpty() ? false : true);
				}

			} catch (HttpHostConnectException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("Blocks in repo: " + blockRepo.count());

		}
		return "Finished syncing.";
	}

}

// batch block queries:
// -use GET HEADERS, start from block 495000
// -get block hashes from header, add to list
// -craft request objects from list
// -post crafted requests in a json batch to daemon

// Instant instantTime = Instant.ofEpochMilli(time * 1000L);
// Timestamp ts = instantTime != null ? new
// Timestamp(instantTime.toEpochMilli()) : null;
