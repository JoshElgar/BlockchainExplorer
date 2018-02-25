package entities;

public class BarChart extends ChartData {

	private static final long serialVersionUID = 8881310070134238244L;

	public int numBlocks, numTx;

	public BarChart(int numBlocks, int numTx) {
		super();
		this.numBlocks = numBlocks;
		this.numTx = numTx;
	}

}
