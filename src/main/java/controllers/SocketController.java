package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import entities.Block;

@Controller
public class SocketController {

	@MessageMapping("/testportal/stomptest")
	@SendTo("/topic/blocks")
	public Block send(Block block) throws Exception {
		System.out.println("Time: " + new SimpleDateFormat("HH:mm").format(new Date()));
		return block;
	}

	/*
	 * @MessageMapping("/testportal/stomptest/info")
	 * 
	 * @SendTo("/topic/blocks") public String sendtwo(@RequestParam("t") String t)
	 * throws Exception { System.out.println("Time: " + new
	 * SimpleDateFormat("HH:mm").format(new Date())); System.out.println("t: " + t);
	 * return "CONNECTED"; }
	 */
}
