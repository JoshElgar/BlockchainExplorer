package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import entities.BarChart;
import entities.Block;
import entities.ChartData;
import entities.TimeChart;
import entities.Transaction;
import repositories.MongoUserRepository;
import repositories.TransactionRepository;

@EnableScheduling
@Service
public class DbService {

	private static final Logger logger = LogManager.getLogger(DbService.class);

	@Autowired
	DataSource ds;

	@Autowired
	public MongoUserRepository mongoRepo;

	@Autowired
	public TransactionRepository txRepo;

	@Autowired
	DaemonService daemonService;

	@Autowired
	private MongoOperations mongo;

	@Scheduled(fixedDelay = 10000)
	public void sync() {

		// mongo.insert(new Block("55555", 55, 100, new Timestamp(500000000)),
		// "blocks");

		logger.info("Syncing database with blockchain.");

		Map<String, Object> chainInfo = daemonService.getChainInfoMap();

		int currentBlocks = (int) chainInfo.get("currentBlocks");
		String blockToGet = (String) chainInfo.get("bestblockhash");
		logger.info("Best block hash retrieved from daemon chaininfo: " + blockToGet);

		int minBlockHeight = currentBlocks - 5; // for testing

		// does repo contain latest block hash
		Block matchingBlock = mongoRepo.findFirstByHash(blockToGet);
		boolean syncedAll = (matchingBlock == null ? false : true);

		if (syncedAll) {
			logger.info("Best block in DB, assume synced.");
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
					// mongoRepo.save(generatedBlock);
					mongo.save(generatedBlock);

					/*
					 * String blockHash = generatedBlock.getHash();
					 * 
					 * for (Transaction t : generatedBlock.getTransactions()) {
					 * t.setBlockHash(blockHash); for (TxVin txVin : t.txVin) {
					 * txVin.setTxHash(t.getHash()); } for (TxVout txVout : t.txVout) {
					 * txVout.setTxHash(t.getHash()); } }
					 * upsertTxs(generatedBlock.getTransactions());
					 * upsertTxVinVout(generatedBlock.getTransactions());
					 */

				}

				blockToGet = generatedBlock.getPrevBlockHash();

			} catch (Exception e) {
				e.printStackTrace();
			}

			logger.info("Blocks in repo: " + mongoRepo.count());

		}
	}

	public List<ChartData> getChartData() {
		List<ChartData> chartData = new ArrayList<ChartData>();

		chartData.add(new BarChart(5, 10));

		List<String> blockHashes = new ArrayList<String>();
		List<Integer> txCounts = new ArrayList<Integer>();
		List<Timestamp> blockTimes = new ArrayList<Timestamp>();

		String sql = "SELECT t.blockhash, COUNT(t.blockhash), b.time " + "FROM transaction AS t "
				+ "LEFT JOIN block AS b ON b.hash=t.blockhash " + "GROUP BY t.blockhash, b.time ORDER BY b.time;";

		Connection conn = null;

		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				blockHashes.add(rs.getString("blockhash"));
				txCounts.add(rs.getInt("count"));
				blockTimes.add(rs.getTimestamp("time"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		chartData.add(new TimeChart(blockHashes, txCounts, blockTimes));

		return chartData;
	}

	public List<Block> getLimitedNewBlocks() {

		List<Block> blocks = mongoRepo.findTop5ByOrderByHeightDesc();

		for (Block b : blocks) {
			b.setNumTx(getNumTxForBlockHash(b.getHash()));
		}

		return blocks;

	}

	public List<Transaction> getLimitedNewTx() {

		Block b = mongoRepo.findFirstByOrderByHeightDesc();

		List<Transaction> txs = new ArrayList<Transaction>();

		if (b != null && b.getTransactions().size() >= 5) {
			txs = b.getTransactions().subList(0, 5);
		}

		return txs;
	}

	/*
	 * Finds newest block in DB, uses daemon api to find it again (updated tx) in
	 * blockchain, saves tx updates, returns top 10 tx based on custom serial id
	 */
	public void updateLastBlock() {

		// finds newest block hash in DB
		Block block = mongoRepo.findFirstByOrderByHeightDesc();
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
				mongoRepo.save(generatedBlock);

			} catch (DataIntegrityViolationException e) {
				logger.info("Block/Tx was not saved - already exists");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int getNumTxForBlockHash(String blockhash) {

		String sql = "SELECT COUNT(*) FROM transaction WHERE blockhash='" + blockhash + "';";

		Connection conn = null;

		int count = 0;

		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {

				count = rs.getInt("count");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return count;
	}

	public Transaction getTxByTxid(String txid) {
		// Block b = mongo.findOne(new
		// Query(Criteria.where("transactions.txid").is(txid)), Transaction.class);
		// b.getTransactions()
		return txRepo.findFirstByTxid(txid);
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
