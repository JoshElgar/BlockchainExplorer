package entities;

public class BarChart extends ChartData {

	private static final long serialVersionUID = 8881310070134238244L;

	public int numPubKey, numPubKeyHash, numScriptHash, numMultiSig, numNullData, numNonStandard;

	public int numRegTx, numCoinbaseTx;

	public BarChart(int numPubKey, int numPubKeyHash, int numScriptHash, int numMultiSig, int numNullData,
			int numNonStandard) {
		super();
		this.numPubKey = numPubKey;
		this.numPubKeyHash = numPubKeyHash;
		this.numScriptHash = numScriptHash;
		this.numMultiSig = numMultiSig;
		this.numNullData = numNullData;
		this.numNonStandard = numNonStandard;

	}

	public BarChart(int numRegTx, int numCoinbaseTx) {
		this.numRegTx = numRegTx;
		this.numCoinbaseTx = numCoinbaseTx;
	}

}
