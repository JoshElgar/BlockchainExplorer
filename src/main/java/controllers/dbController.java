package controllers;

import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import entities.Block;
import entities.BlockDateDeserialiser;
import entities.Transaction;
import repositories.BlockRepository;
import repositories.TransactionRepository;

@Controller
public class DbController {

	@Autowired
	BlockRepository blockRepo;

	@Autowired
	TransactionRepository transactionRepo;

	@RequestMapping(value = "/testportal/sync")
	@ResponseBody
	public String sync() {

		System.out.println("Syncing block.");

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse blockResponse, chainResponse;
		int currentBlocks = 0;
		String blockToGet = "";

		// get block height + latest block hash
		HttpGet httpChainInfo = new HttpGet("http://localhost:8332/rest/chaininfo.json");
		try {
			chainResponse = httpclient.execute(httpChainInfo);
			String chainInfo = EntityUtils.toString(chainResponse.getEntity());

			JsonParser jsonParser = new JsonParser();
			JsonElement jsonTree = jsonParser.parse(chainInfo);

			if (jsonTree.isJsonObject()) {
				JsonObject jsonObject = jsonTree.getAsJsonObject();
				currentBlocks = jsonObject.get("blocks").getAsInt();
				blockToGet = jsonObject.get("bestblockhash").getAsString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// does repo contain latest block hash
		List<Block> matchingBlocks = blockRepo.findByHash(blockToGet);

		int minBlockHeight = currentBlocks - 20; // for testing
		// there are no matching blocks in DB + wrong count in DB
		boolean syncedAll = (!(matchingBlocks.isEmpty()) && (blockRepo.count() == (currentBlocks - minBlockHeight)));

		while (!syncedAll) {
			try {

				// get next block
				HttpGet httpgetBlock = new HttpGet("http://localhost:8332/rest/block/" + blockToGet + ".json");
				blockResponse = httpclient.execute(httpgetBlock);
				String blockInfo = EntityUtils.toString(blockResponse.getEntity());

				if (blockInfo.contains("Verifying blocks")) {
					System.out.println("Bitcoind still syncing with network.");
				} else {

					Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new BlockDateDeserialiser()).create();
					Block generatedBlock = gson.fromJson(blockInfo, Block.class);

					if (generatedBlock.getHeight() < minBlockHeight) {
						System.out.println("Block height less than min block height");
						syncedAll = true;
						return "Synced all.";
					} else {
						// sync transactions

						System.out.println("Saving block with hash:" + generatedBlock.getHash());
						blockRepo.save(generatedBlock);

						System.out.println("Saving all transactions of block:" + generatedBlock.getHash());
						String blockHash = generatedBlock.getHash();
						for (Transaction t : generatedBlock.getTransactions()) {
							t.setBlockHash(blockHash);
						}

						transactionRepo.save(generatedBlock.getTransactions());
						System.out.println("Finished Syncing");

					}

					blockToGet = generatedBlock.getPrevBlockHash();
					matchingBlocks = blockRepo.findByHash(blockToGet);
					System.out.println("Blocks in repo: " + blockRepo.count());
					syncedAll = (!(matchingBlocks.isEmpty())
							&& (blockRepo.count() == (currentBlocks - minBlockHeight)));
				}
			} catch (HttpHostConnectException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return "Finished syncing.";
	}

}

// Instant instantTime = Instant.ofEpochMilli(time * 1000L);
// Timestamp ts = instantTime != null ? new
// Timestamp(instantTime.toEpochMilli()) : null;
