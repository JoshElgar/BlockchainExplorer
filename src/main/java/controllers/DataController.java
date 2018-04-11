package controllers;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entities.Block;
import entities.ChartData;
import entities.Transaction;
import services.DaemonService;
import services.DbService;

@RestController
public class DataController {

	private static final Logger logger = LogManager.getLogger(DataController.class);

	@Autowired
	public DbService dbService;

	@Autowired
	public DaemonService daemonService;

	@RequestMapping(value = "/api/data/getdata")
	public List<ChartData> getChartData() {
		logger.info("DataController ::: Getting chart data.");

		return dbService.getChartData();

	}

	@RequestMapping(value = "/api/search/{value}")
	public Map<String, String> searchDB(@PathVariable("value") String value) {
		logger.info("Searching for value: " + value);

		Map<String, String> result = dbService.searchFor(value);
		return result;
	}

	@RequestMapping(value = "/api/getobj/block/{blockHash}")
	public Object getBlock(@PathVariable("blockHash") String blockHash) {
		logger.info("Trying to get block with hash: " + blockHash);

		// try and retrieve block from DB, fallback to querying daemon
		Block block = dbService.blockRepo.findFirstByHash(blockHash);

		if (block == null) {
			block = daemonService.getBlockByHash(blockHash);
			if (block == null) {
				return "null";
			}
		}

		return block;

	}

	@RequestMapping(value = "/api/getobj/tx/{value}")
	public Object getTx(@PathVariable("value") String value) {
		logger.info("Trying to get tx with value: " + value);

		Transaction tx = dbService.getTx(value);

		if (tx == null) {
			tx = daemonService.getTxByTxid(value);
			if (tx == null) {
				return "null";
			}
		}

		return tx;

	}

}
