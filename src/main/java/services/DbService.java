package services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import entities.BarChart;
import entities.Block;
import entities.ChainInfo;
import entities.ChartData;
import entities.TimeChart;
import entities.Transaction;
import entities.TxVin;
import repositories.BlockUserRepository;

/*
 * This service provides database services to the rest of the application - performing regular database-daemon syncs, updating + retrieval of blocks/txs and data for the chart page.
 */
@EnableScheduling
@Service
public class DbService {

	private static final Logger logger = LogManager.getLogger(DbService.class);

	@Autowired
	public BlockUserRepository blockRepo;

	@Autowired
	DaemonService daemonService;

	/*
	 * Syncs the database using the daemon - finds the best blockhash, gets the 5
	 * previous blocks sequentially.
	 */
	@Scheduled(fixedDelay = 10000)
	public void sync() {

		logger.info("Syncing database with blockchain.");

		ChainInfo chainInfo = daemonService.getChainInfo();

		int currentBlocks = chainInfo.getBlocks();
		String blockToGet = chainInfo.getBestblockhash();
		logger.info("Best block hash retrieved from daemon chaininfo: " + blockToGet);

		int minBlockHeight = currentBlocks - 5; // for testing

		// does repo contain latest block hash
		Block matchingBlock = blockRepo.findFirstByHash(blockToGet);
		boolean syncedAll = (((matchingBlock == null) || (blockRepo.count()) < 5) ? false : true);

		if (syncedAll) {
			logger.info("Best block in DB + more than 5 blocks, assumed synced.");
		} else {
			logger.info("Best block not found in DB.");
		}

		while (!syncedAll) {
			try {

				Block generatedBlock = daemonService.getBlockByHash(blockToGet);

				if (generatedBlock.getHeight() < minBlockHeight) {
					logger.info("Not one of the last 5 blocks in blockchain, finished syncing.");
					syncedAll = true;

				} else {
					logger.info("Saving block with hash:" + generatedBlock.getHash());
					logger.info("Block time: " + generatedBlock.getTime());
					blockRepo.save(generatedBlock);
				}

				blockToGet = generatedBlock.getPrevBlockHash();

			} catch (Exception e) {
				e.printStackTrace();
			}

			logger.info("Blocks in repo: " + blockRepo.count());

		}
	}

	/*
	 * Retrieves a List<ChartData> for the chart page. NOTE: The order is important:
	 * 1 - Num Reg/Coinbase Tx Bar Chart, 2 - Block Time Chart with Tx counts
	 */
	public List<ChartData> getChartData() {
		List<ChartData> chartData = new ArrayList<ChartData>();

		List<String> blockHashes = new ArrayList<String>();
		List<Integer> txCounts = new ArrayList<Integer>();
		List<Date> blockTimes = new ArrayList<Date>();

		List<Block> allBlocks = blockRepo.findAll();

		int numCoinbaseTx = 0;
		int numRegTx = 0;

		/*
		 * Sort all blocks by time, newest first
		 */
		Collections.sort(allBlocks, new Comparator<Block>() {
			@Override
			public int compare(Block b1, Block b2) {
				return b1.getTime().compareTo(b2.getTime());
			}
		});

		for (Block b : allBlocks) {
			blockHashes.add(b.getHash());

			List<Transaction> txs = b.getTransactions();

			int blockTxs = txs.size();
			b.setNumTx(blockTxs);

			txCounts.add(blockTxs);

			for (Transaction t : txs) {
				for (TxVin txVin : t.getTxVin()) {
					if (txVin.getCoinbase() == null) {
						numRegTx++;
					} else {
						numCoinbaseTx++;
					}
				}
			}

			blockTimes.add(b.getTime());

		}

		logger.info("Reg tx found: " + numRegTx);
		logger.info("Coinbase tx found: " + numCoinbaseTx);

		chartData.add(new BarChart(numRegTx, numCoinbaseTx));
		chartData.add(new TimeChart(blockHashes, txCounts, blockTimes));

		return chartData;
	}

	/*
	 * Gets a limited number of the newest blocks to be streamed to the home page.
	 */
	public List<Block> getLimitedNewBlocks() {

		List<Block> blocks = blockRepo.findTop5ByOrderByHeightDesc();

		for (Block b : blocks) {
			b.setNumTx(b.getTransactions().size());
		}

		return blocks;

	}

	/*
	 * Gets a limited number of the newest transactions to be streamed to the home
	 * page.
	 */
	public List<Transaction> getLimitedNewTx() {

		Block b = blockRepo.findFirstByOrderByHeightDesc();

		List<Transaction> txs = new ArrayList<Transaction>();

		if (b != null && b.getTransactions().size() >= 10) {
			txs = b.getTransactions().subList(0, 10);
		}

		return txs;
	}

	/*
	 * Finds newest block in DB, uses daemon api to find it again (updated tx) in
	 * blockchain, saves tx updates, returns top 10 tx based on custom serial id
	 */
	public void updateLastBlock() {

		// finds newest block hash in DB
		Block block = blockRepo.findFirstByOrderByHeightDesc();
		if (block == null) {

		} else {
			logger.info("Refreshing block hash: " + block.getHash());
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse blockResponse;

			HttpGet httpgetBlock = new HttpGet("http://localhost:8332/rest/block/" + block.getHash() + ".json");
			try {
				blockResponse = httpclient.execute(httpgetBlock);
				String blockInfo = EntityUtils.toString(blockResponse.getEntity());

				ObjectMapper mapper = new ObjectMapper();
				Block generatedBlock = mapper.readValue(blockInfo, Block.class);
				blockRepo.save(generatedBlock);

			} catch (DataIntegrityViolationException e) {
				logger.info("Block/Tx was not saved - already exists");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Gets a transaction from the database based on a value (can be either a txid
	 * or tx hash)
	 */
	public Transaction getTx(String value) {

		Block b = blockRepo.findBlockContainingTxWithTxid(value);

		if (b != null) {
			List<Transaction> txs = b.getTransactions().stream().filter(t -> t.getTxid().equals(value))
					.collect(Collectors.toList());
			Transaction t = txs.get(0);
			t.setBlockHash(b.getHash());
			return t;
		} else {
			b = blockRepo.findBlockContainingTxWithHash(value);
			if (b != null) {
				List<Transaction> txs = b.getTransactions().stream().filter(t -> t.getHash().equals(value))
						.collect(Collectors.toList());
				Transaction t = txs.get(0);
				t.setBlockHash(b.getHash());
				return t;
			}
		}

		return new Transaction();

	}

	/*
	 * Implements the search function: looks for blocks or txs with fields could
	 * match a given value. Returns a map of type and the block hash or txid
	 * required for JS to navigate to right page.
	 */
	public Map<String, String> searchFor(String value) {

		Map<String, String> result = new HashMap<String, String>();

		// try and find matching block or tx
		if (blockRepo.findFirstByHash(value) != null) {
			result.put("block", value);
		} else {
			Block b = blockRepo.findBlockContainingTxWithHash(value);
			if (b != null) {
				String txid = b.getTransactions().stream().filter(t -> t.getHash().equals(value))
						.collect(Collectors.toList()).get(0).getTxid();
				result.put("tx", txid);
			} else if (blockRepo.findBlockContainingTxWithTxid(value) != null) {
				result.put("tx", value);
			} else {
				result.put("none", "none");
			}
		}

		logger.info("Search for found result: " + result);

		return result;

	}

}

// batch block queries:
// -use GET HEADERS, start from block 495000
// -get block hashes from header, add to list
// -craft request objects from list
// -post crafted requests in a json batch to daemon
