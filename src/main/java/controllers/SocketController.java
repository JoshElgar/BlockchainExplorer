package controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import entities.Block;

@EnableScheduling
@Controller
public class SocketController {

	@Autowired
	private SimpMessagingTemplate template;

	@Scheduled(fixedDelay = 5000)
	public void stream() {

		System.out.println("scheduled");
		List<Block> returnBlocks = new ArrayList<Block>();
		returnBlocks.add(new Block("block1"));
		returnBlocks.add(new Block("block2"));
		this.template.convertAndSend("/topic/blocks", returnBlocks);

	}
}
