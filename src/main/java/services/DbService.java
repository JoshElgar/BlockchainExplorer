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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import entities.Block;
import entities.ChartData;
import entities.Transaction;
import repositories.BlockRepository;
import repositories.TransactionRepository;

@EnableScheduling
@Service
public class DbService {

	@Autowired
	DataSource ds;

	@Autowired
	public BlockRepository blockRepo;

	@Autowired
	public TransactionRepository txRepo;

	@Autowired
	DaemonService daemonService;

	@Scheduled(fixedDelay = 20000)
	public void sync() {

		System.out.println("SYNC ::: Syncing database with blockchain.");

		Map<String, Object> chainInfo = daemonService.getChainInfoMap();

		int currentBlocks = (int) chainInfo.get("currentBlocks");
		String blockToGet = (String) chainInfo.get("bestblockhash");

		int minBlockHeight = currentBlocks - 5; // for testing

		// does repo contain latest block hash
		Block matchingBlock = blockRepo.findFirstByHash(blockToGet);
		boolean syncedAll = (matchingBlock == null ? false : true);

		while (!syncedAll) {
			try {

				Block generatedBlock = daemonService.getBlockByHash(blockToGet);

				if (generatedBlock.getHeight() < minBlockHeight) {
					System.out.println("SYNC ::: Not one of the last 5 blocks in blockchain, finished syncing.");
					syncedAll = true;

				} else {

					System.out.println("SYNC ::: Saving block with hash:" + generatedBlock.getHash());
					blockRepo.save(generatedBlock);
					List<Transaction> txs = new ArrayList<Transaction>();
					for (Transaction t : generatedBlock.getTransactions()) {
						t = daemonService.getTxByTxid(t.getTxid());
						if (t != null) {
							txs.add(t);
						}
					}
					upsertTxs(txs);

				}

				blockToGet = generatedBlock.getPrevBlockHash();

			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("SYNC ::: Blocks in repo: " + blockRepo.count());

		}
	}

	public ChartData getChartData() {

		int numBlocks = 0;
		int numTx = 0;

		return new ChartData(10, 1000);
	}

	public List<Block> getLimitedNewBlocks(int limit) {

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
		String sql = "SELECT serialid, hash FROM transaction ORDER BY serialid DESC LIMIT ";
		sql = sql.concat(String.valueOf(limit) + ";");

		Connection conn = null;
		List<Transaction> txs = new ArrayList<Transaction>();
		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String hash = rs.getString("hash");
				int id = rs.getInt("serialid");

				txs.add(new Transaction(hash, id));
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

		String sql = "INSERT INTO transaction (blockhash, bytesize, hash, txid, version, confirmations, time) VALUES";

		// Txs = Txs.subList(0, 3);

		for (Transaction t : Txs) {
			sql = sql.concat(" ('" + t.getBlockHash() + "', " + t.getByteSize() + ", '" + t.getHash() + "', '"
					+ t.getTxid() + "', " + t.getVersion() + ", " + t.getConfirmations() + ", " + t.getTime() + "),");
		}

		// System.out.println("\n\n\nSQL: " + sql + "\n\n\n");

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

	/*
	 * Finds newest block in DB, uses daemon api to find it again (updated tx) in
	 * blockchain, saves tx updates, returns top 10 tx based on custom serial id
	 */
	public void updateLastBlock() {

		// finds newest block hash in DB
		Block block = blockRepo.findFirstByOrderByHeightDesc();
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
