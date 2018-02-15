package controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import entities.Block;
import entities.Transaction;
import services.DbService;

@EnableScheduling
@Controller
public class SocketController {

	@Autowired
	private DbService dbService;

	private int lastBlockHeight = 0;
	private int lastTxSerialId = 0;

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

		List<Block> returnBlocks = dbService.getLimitedNewBlocks(5);

		System.out.println("Current last block height before filter: " + lastBlockHeight);
		List<Block> filteredBlocks = returnBlocks.stream().filter(block -> block.getHeight() > lastBlockHeight)
				.collect(Collectors.toList());

		if (filteredBlocks.isEmpty()) {
			System.out.println("No new blocks to stream.");
		} else {
			this.template.convertAndSend("/topic/blocks", filteredBlocks);
		}

	}

	@Scheduled(fixedDelay = 10000)
	public void streamLastTxs() {

		System.out.println("\nStreaming new txs.");

		dbService.updateLastBlock();

		List<Transaction> returnTx = dbService.getLimitedNewTx(10);

		System.out.println("Current last tx serial before filter: " + lastTxSerialId);
		List<Transaction> filteredTx = returnTx.stream().filter(tx -> tx.getSerialid() > lastTxSerialId)
				.collect(Collectors.toList());

		if (filteredTx.isEmpty()) {
			System.out.println("No new tx to stream.");
		} else {
			this.template.convertAndSend("/topic/tx", filteredTx);
		}

	}

}
