package controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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

	private static final Logger logger = LogManager.getLogger(SocketController.class);

	@Autowired
	private DbService dbService;

	private int lastBlockHeight = 0;
	private int lastTxSerialId = 0;

	@Autowired
	private SimpMessagingTemplate template;

	@MessageMapping("/socket/updateLastBlockHeight")
	public void updateLastBlockHeight(Message<String> msg) {
		lastBlockHeight = Integer.parseInt(msg.getPayload());
		logger.info("New block height: " + lastBlockHeight);
	}

	@MessageMapping("/socket/updateLastTxSerial")
	public void updateLastTxSerial(Message<String> msg) {
		lastTxSerialId = Integer.parseInt(msg.getPayload());
		logger.info("New tx serial: " + lastTxSerialId);
	}

	@Scheduled(fixedDelay = 10000)
	public void streamLastBlocks() {

		logger.info("Streaming last blocks:");

		List<Block> returnBlocks = dbService.getLimitedNewBlocks();

		logger.info("Last block received by client: " + lastBlockHeight);
		List<Block> filteredBlocks = returnBlocks.stream().filter(block -> block.getHeight() > lastBlockHeight)
				.collect(Collectors.toList());

		if (filteredBlocks.isEmpty()) {
			logger.info("No new blocks to stream.");
		} else {
			this.template.convertAndSend("/topic/blocks", filteredBlocks);
		}

	}

	@Scheduled(fixedDelay = 10000)
	public void streamLastTxs() {

		logger.info("Streaming new txs.");

		dbService.updateLastBlock();

		List<Transaction> returnTx = dbService.getLimitedNewTx();

		if (returnTx.isEmpty()) {
			logger.info("No new tx to stream.");
		} else {
			this.template.convertAndSend("/topic/tx", returnTx);
		}

	}

}
