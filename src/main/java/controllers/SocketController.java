package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import entities.Block;
import entities.Transaction;
import repositories.BlockRepository;
import repositories.TransactionRepository;

@EnableScheduling
@Controller
public class SocketController {

	private int lastBlockHeight = 0;
	private int lastTxSerialId = 0;

	@Autowired
	BlockRepository blockRepo;

	@Autowired
	TransactionRepository txRepo;

	@Autowired
	DataSource ds;

	@Autowired
	private SimpMessagingTemplate template;

	@MessageMapping("/socket/updateLastBlockHeight")
	public void updateLastBlockHeight(Message<String> msg) {
		lastBlockHeight = Integer.parseInt(msg.getPayload());
		System.out.println("New block height: " + lastBlockHeight);
	}

	@MessageMapping("/socket/updateLastTxSerial")
	public void updateLastTxSerial(Message<String> msg) {
		lastTxSerialId = Integer.parseInt(msg.getPayload());
		System.out.println("New tx serial: " + lastTxSerialId);
	}

	@Scheduled(fixedDelay = 10000)
	public void streamLastBlocks() {

		System.out.println("\nStreaming last blocks:");

		List<Block> returnBlocks = getNewBlocks();

		System.out.println("Current last block height before filter: " + lastBlockHeight);
		List<Block> filteredBlocks = returnBlocks.stream().filter(block -> block.getHeight() > lastBlockHeight)
				.collect(Collectors.toList());

		if (filteredBlocks.isEmpty()) {
			System.out.println("No new blocks to stream.");
		} else {
			this.template.convertAndSend("/topic/blocks", filteredBlocks);
		}

	}

	@Scheduled(fixedDelay = 3000)
	public void streamLastTxs() {

		System.out.println("\nStreaming new txs.");

		updateLastBlock();

		List<Transaction> returnTx = getNewTx();

		System.out.println("Current last tx serial before filter: " + lastTxSerialId);
		List<Transaction> filteredTx = returnTx.stream().filter(tx -> tx.getSerialid() > lastTxSerialId)
				.collect(Collectors.toList());

		if (filteredTx.isEmpty()) {
			System.out.println("No new tx to stream.");
		} else {
			this.template.convertAndSend("/topic/tx", filteredTx);
		}

	}

	public List<Block> getNewBlocks() {

		// top 5 blocks on height
		String sql = "SELECT hash, height, time FROM block ORDER BY height DESC LIMIT 5;";

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

				blocks.add(new Block(height, numTx, time));
				System.out.println("Potential block added: " + height + " " + numTx + " " + time);

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

	private List<Transaction> getNewTx() {
		// top 5 blocks on height
		String sql = "SELECT serialid, hash FROM transaction ORDER BY serialid DESC LIMIT 10;";

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
				System.out.println("Potential tx added: " + hash + " " + id);

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

	public void upsertTxs(List<Transaction> Txs) {

		String sql = "INSERT INTO transaction (blockhash, bytesize, hash, txid, version) VALUES";

		// Txs = Txs.subList(0, 3);

		for (Transaction t : Txs) {
			sql = sql.concat(" ('" + t.getBlockHash() + "', " + t.getBytesize() + ", '" + t.getHash() + "', '"
					+ t.getTxid() + "', " + t.getVersion() + "),");
		}

		// System.out.println("\n\n\nSQL: " + sql + "\n\n\n");

		sql = sql.substring(0, sql.length() - 1); // remove trailing comma

		sql = sql.concat(" ON CONFLICT (hash) DO NOTHING;");

		Connection conn = null;

		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			int returnCode = ps.executeUpdate();

			System.out.println("Return code: " + returnCode);
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
	 * Finds newest block in DB, uses api to find it again (updated tx) in
	 * blockchain, saves tx updates, returns top 10 tx based on custom serial id
	 */
	private void updateLastBlock() {

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

				System.out.println("block num txs: " + generatedBlock.getTransactions().size());

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

}
