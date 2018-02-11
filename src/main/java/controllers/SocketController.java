package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;

import entities.Block;

@EnableScheduling
@Controller
public class SocketController {

	private int lastBlockHeight = 0;

	@Autowired
	DataSource ds;

	@Autowired
	private SimpMessagingTemplate template;

	public void streamLastBlocks() {

		System.out.println("Streaming last blocks:");
		// List<Block> returnBlocks = new ArrayList<Block>();
		// returnBlocks.add(new Block("block1"));
		// returnBlocks.add(new Block("block2"));
		List<Block> returnBlocks = getLastBlocksStream();
		this.template.convertAndSend("/topic/blocks", returnBlocks);

	}

	public List<Block> getLastBlocksStream() {

		// top 5 blocks on height
		String sql = "SELECT hash, height, time FROM block ORDER BY height DESC LIMIT 5;";

		Connection conn = null;
		List<Block> blocks = new ArrayList<Block>();
		try {
			conn = ds.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			System.out.println("PS executed.");
			while (rs.next()) {

				String hash = rs.getString("hash");
				int height = rs.getInt("height");
				Timestamp time = rs.getTimestamp("time");
				int numTx = getNumTxForBlockHash(hash);

				// if (height > lastBlockHeight) {

				blocks.add(new Block(height, numTx, time));
				System.out.println("Block added: " + height + " " + numTx + " " + time);

				// }

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
		if (!(blocks.isEmpty())) {
			lastBlockHeight = blocks.get(0).getHeight();
		}

		return blocks;

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
