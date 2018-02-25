package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entities.ChartData;
import services.DbService;

@RestController
public class DataController {

	@Autowired
	public DbService dbService;

	@RequestMapping(value = "/data/getchartdata")
	public List<ChartData> getChartData() {
		System.out.println("DataController ::: Getting chart data.");

		return dbService.getChartData();

	}

}
