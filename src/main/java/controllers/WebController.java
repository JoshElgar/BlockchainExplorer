package controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import entities.Block;
import services.DaemonService;
import services.DbService;

@Controller
public class WebController {

	@Autowired
	DbService dbService;

	@Autowired
	DaemonService daemonService;

	@RequestMapping(value = { "/", "/home" })
	public String home(Model m) {
		System.out.println("Serving home.html");
		return "home";
	}

	@RequestMapping(value = "/block")
	public String servePage(@PathVariable("blockHash") String blockHash, Model m) {
		System.out.println("Serving block.html");

		Map<String, Object> attribs = new HashMap<String, Object>();

		// try and retrieve block from DB, fallback to querying daemon

		Block block = dbService.blockRepo.findFirstByHash(blockHash);

		if (block == null) {
			block = daemonService.getBlockByHash(blockHash);
			if (block == null) {
				attribs.put("found", false);
				m.addAllAttributes(attribs);
				return "block";
			}
		}

		attribs.put("found", true);
		attribs.put("block", block);
		m.addAllAttributes(attribs);

		return "block";

	}

	@RequestMapping(value = "/testportal")
	public String testportal(Model m) {
		System.out.println("Serving testportal.html");
		return "testPortal";
	}

}
