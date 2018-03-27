package entities;

import java.util.Date;
import java.util.List;

public class TimeChart extends ChartData {

	private static final long serialVersionUID = -1515175146425363881L;

	public List<String> blockHash;
	public List<Integer> txCount;
	public List<Date> blockTime;

	public TimeChart(List<String> blockHash, List<Integer> txCount, List<Date> blockTime) {
		super();
		this.blockHash = blockHash;
		this.txCount = txCount;
		this.blockTime = blockTime;
	}
}
