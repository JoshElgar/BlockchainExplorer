package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
import entities.TxVin;
import entities.TxVout;
import repositories.MongoUserRepository;
import repositories.TransactionRepository;

@EnableScheduling
@Service
public class DbService {

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

		System.out.println("SYNC ::: Syncing database with blockchain.");

		Map<String, Object> chainInfo = daemonService.getChainInfoMap();

		int currentBlocks = (int) chainInfo.get("currentBlocks");
		String blockToGet = (String) chainInfo.get("bestblockhash");

		int minBlockHeight = currentBlocks - 5; // for testing

		// does repo contain latest block hash
		Block matchingBlock = mongoRepo.findFirstByHash(blockToGet);
		boolean syncedAll = (matchingBlock == null ? false : true);

		while (!syncedAll) {
			try {

				Block generatedBlock = daemonService.getBlockByHash(blockToGet);

				if (generatedBlock.getHeight() < minBlockHeight) {
					System.out.println("SYNC ::: Not one of the last 5 blocks in blockchain, finished syncing.");
					syncedAll = true;

				} else {

					System.out.println("SYNC ::: Saving block with hash:" + generatedBlock.getHash());
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

			System.out.println("SYNC ::: Blocks in repo: " + mongoRepo.count());

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

	public List<Block> getLimitedNewBlocks(int limit) {
		
		mongo.f

		// top 5 blocks on height
		String sql = "SELECT hash, height, time FROM block ORDER BY height DESC LIMIT ";
		sql = sql.concat(String.valueOf(limit) + ";");

		Connection conn = null;
		List<Block> blocks = new ArrayList<Block>();
		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String hash = rs.getString("hash");
				int height = rs.getInt("height");
				Timestamp time = rs.getTimestamp("time");
				int numTx = getNumTxForBlockHash(hash);

				blocks.add(new Block(hash, height, numTx, time));
				// System.out.println("Potential block added: " + height + " " + numTx + " " +
				// time);

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

		return blocks;

	}

	public List<Transaction> getLimitedNewTx(int limit) {
		// top 5 blocks on height
		String sql = "SELECT hash FROM transaction ORDER BY serialid DESC LIMIT ";
		sql = sql.concat(String.valueOf(limit) + ";");

		Connection conn = null;
		List<Transaction> txs = new ArrayList<Transaction>();
		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String hash = rs.getString("hash");

				txs.add(new Transaction(hash));
				// System.out.println("Potential tx added: " + hash + " " + id);

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

		return txs;
	}

	public void upsertTxs(List<Transaction> Txs) {

		String sql = "INSERT INTO transaction (blockhash, bytesize, hash, txid, version) VALUES";

		for (Transaction t : Txs) {
			sql = sql.concat(" ('" + t.getBlockHash() + "', " + t.getByteSize() + ", '" + t.getHash() + "', '"
					+ t.getTxid() + "', " + t.getVersion() + "),");
		}

		sql = sql.substring(0, sql.length() - 1); // remove trailing comma

		sql = sql.concat(" ON CONFLICT (hash) DO NOTHING;");

		Connection conn = null;

		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();

			// System.out.println("Return code: " + returnCode);
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
	}

	public void upsertTxVinVout(List<Transaction> Txs) {

		Txs = Txs.subList(0, 3);
		String coinbaseVinSql = "INSERT INTO txvin (txhash, coinbase, vinsequence) VALUES";
		String regularVinSql = "INSERT INTO txvin (txhash, txid, vout, vinsequence) VALUES";
		String regularVoutSql = "INSERT INTO txvout (txhash, value) VALUES";

		int coinbaseVinsCount = 0;
		int regularVinsCount = 0;
		int regularVoutsCount = 0;

		for (Transaction t : Txs) {

			List<TxVin> coinbaseVins = t.txVin.stream().filter(txVin -> txVin.isCoinbase())
					.collect(Collectors.toList());

			List<TxVin> regularVins = t.txVin.stream().filter(txVin -> !txVin.isCoinbase())
					.collect(Collectors.toList());

			coinbaseVinsCount += coinbaseVins.size();
			regularVinsCount += regularVins.size();
			regularVoutsCount += t.txVout.size();

			for (TxVin txVin : coinbaseVins) {
				coinbaseVinSql = coinbaseVinSql
						.concat(" ('" + txVin.txhash + "', '" + txVin.coinbase + "', " + txVin.vinsequence + "),");
			}

			for (TxVin txVin : regularVins) {
				regularVinSql = regularVinSql.concat(" ('" + txVin.getTxHash() + "', '" + txVin.txid + "', "
						+ txVin.vout + ", " + txVin.getSequence() + "),");
			}

			for (TxVout txVout : t.txVout) {
				regularVoutSql = regularVoutSql.concat(" ('" + txVout.getTxHash() + "', " + txVout.getValue() + "),");
			}

		}

		System.out.println("DBSERVICE : UPSERTTXVINVOUT : NUM OF VINS/VOUTS: " + coinbaseVinsCount + " "
				+ regularVinsCount + " " + regularVoutsCount);

		coinbaseVinSql = coinbaseVinSql.substring(0, coinbaseVinSql.length() - 1); // remove trailing comma
		regularVinSql = regularVinSql.substring(0, regularVinSql.length() - 1); // remove trailing comma
		regularVoutSql = regularVoutSql.substring(0, regularVoutSql.length() - 1); // remove trailing comma

		Connection conn = null;

		try {
			conn = ds.getConnection();
			if (coinbaseVinsCount > 0) {
				PreparedStatement coinbaseVinPs = conn.prepareStatement(coinbaseVinSql);
				coinbaseVinPs.executeUpdate();
			}
			if (regularVinsCount > 0) {
				PreparedStatement regularVinPs = conn.prepareStatement(regularVinSql);
				regularVinPs.executeUpdate();
			}
			if (regularVoutsCount > 0) {
				PreparedStatement regularVoutPs = conn.prepareStatement(regularVoutSql);
				regularVoutPs.executeUpdate();
			}

			// System.out.println("Return code: " + returnCode);
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
			System.out.println("Refreshing block hash: " + block.getHash());
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse blockResponse;

			HttpGet httpgetBlock = new HttpGet("http://localhost:8332/rest/block/" + block.getHash() + ".json");
			try {
				blockResponse = httpclient.execute(httpgetBlock);
				String blockInfo = EntityUtils.toString(blockResponse.getEntity());

				ObjectMapper mapper = new ObjectMapper();
				Block generatedBlock = mapper.readValue(blockInfo, Block.class);

				// System.out.println("block num txs: " +
				// generatedBlock.getTransactions().size());

				// blockRepo.save(generatedBlock);

				String blockHash = generatedBlock.getHash();
				for (Transaction t : generatedBlock.getTransactions()) {
					t.setBlockHash(blockHash);
				}

				// txRepo.save(generatedBlock.getTransactions());
				upsertTxs(generatedBlock.getTransactions());

			} catch (DataIntegrityViolationException e) {
				System.out.println("Block/Tx was not saved - already exists");
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
}

// batch block queries:
// -use GET HEADERS, start from block 495000
// -get block hashes from header, add to list
// -craft request objects from list
// -post crafted requests in a json batch to daemon

// Instant instantTime = Instant.ofEpochMilli(time * 1000L);
// Timestamp ts = instantTime != null ? new
// Timestamp(instantTime.toEpochMilli()) : null;
