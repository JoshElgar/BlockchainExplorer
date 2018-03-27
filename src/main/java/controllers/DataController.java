package controllers;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entities.ChartData;
import services.DbService;

@RestController
public class DataController {

	private static final Logger logger = LogManager.getLogger(DataController.class);

	@Autowired
	public DbService dbService;

	@RequestMapping(value = "/data/getchartdata")
	public List<ChartData> getChartData() {
		logger.info("DataController ::: Getting chart data.");

		return dbService.getChartData();

	}

	@RequestMapping(value = "/search/{value}")
	public Map<String, String> searchDB(@PathVariable("value") String value) {
		logger.info("Searching for value: " + value);

		Map<String, String> result = dbService.searchFor(value);
		return result;
	}

}
