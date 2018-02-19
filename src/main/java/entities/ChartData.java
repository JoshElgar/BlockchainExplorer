package entities;

import java.io.Serializable;

public class ChartData implements Serializable {

	private static final long serialVersionUID = 1L;

	public ChartData(int numBlocks, int numTx) {
		super();
		this.numBlocks = numBlocks;
		this.numTx = numTx;
	}

	public int numBlocks, numTx;

}
