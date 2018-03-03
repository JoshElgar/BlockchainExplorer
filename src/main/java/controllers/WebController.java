package controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import entities.Block;
import entities.Transaction;
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

	@RequestMapping(value = "/data")
	public String data(Model m) {
		System.out.println("Serving data.html");

		return "data";
	}

	@RequestMapping(value = "/block/{blockHash}")
	public String serveBlockPage(@PathVariable("blockHash") String blockHash, Model m) {
		System.out.println("Serving block.html");

		Map<String, Object> attribs = new HashMap<String, Object>();

		switch (blockHash) {
		case "test":
			blockHash = "00000000000000000024fb37364cbf81fd49cc2d51c09c75c35433c3a1945d04";
		}

		// try and retrieve block from DB, fallback to querying daemon
		Block block = dbService.mongoRepo.findFirstByHash(blockHash);

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

	@RequestMapping(value = "/tx/{txHash}")
	public String serveTxPage(@PathVariable("txHash") String txHash, Model m) {
		System.out.println("Serving tx.html");

		Map<String, Object> attribs = new HashMap<String, Object>();

		switch (txHash) {
		case "test":
			txHash = "00000000000000000024fb37364cbf81fd49cc2d51c09c75c35433c3a1945d04";
		}

		// try and retrieve block from DB, fallback to querying daemon
		// Transaction tx = dbService.txRepo.findFirstByHash(txHash);
		Transaction tx = daemonService.getTxByTxid(txHash);
		attribs.put("tx", tx);
		m.addAllAttributes(attribs);

		return "tx";

	}

	@RequestMapping(value = "/testportal")
	public String testportal(Model m) {
		System.out.println("Serving testportal.html");
		return "testPortal";
	}

}
