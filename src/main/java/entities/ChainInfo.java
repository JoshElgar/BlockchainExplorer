package entities;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChainInfo implements Serializable {

	private static final long serialVersionUID = -9117119653503606591L;

	@JsonProperty("chain")
	private String chain;

	@JsonProperty("blocks")
	private int blocks;

	@JsonProperty("headers")
	private int headers;

	@JsonProperty("bestblockhash")
	private String bestblockhash;

	@JsonProperty("difficulty")
	private double difficulty;

	@JsonProperty("mediantime")
	private Date mediantime;

	@JsonProperty("verificationprogress")
	private double verificationprogress;

	@JsonProperty("chainwork")
	private String chainwork;

	@JsonProperty("pruned")
	private String pruned;

	public ChainInfo() {

	}

	public ChainInfo(String chain, int blocks, int headers, String bestblockhash, double difficulty, Date mediantime,
			double verificationprogress, String chainwork, String pruned) {
		this.chain = chain;
		this.blocks = blocks;
		this.headers = headers;
		this.bestblockhash = bestblockhash;
		this.difficulty = difficulty;
		this.mediantime = mediantime;
		this.verificationprogress = verificationprogress;
		this.chainwork = chainwork;
		this.pruned = pruned;

	}

	public String getChain() {
		return chain;
	}

	public void setChain(String chain) {
		this.chain = chain;
	}

	public int getBlocks() {
		return blocks;
	}

	public void setBlocks(int blocks) {
		this.blocks = blocks;
	}

	public int getHeaders() {
		return headers;
	}

	public void setHeaders(int headers) {
		this.headers = headers;
	}

	public String getBestblockhash() {
		return bestblockhash;
	}

	public void setBestblockhash(String bestblockhash) {
		this.bestblockhash = bestblockhash;
	}

	public double getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(double difficulty) {
		this.difficulty = difficulty;
	}

	public Date getMediantime() {
		return mediantime;
	}

	public void setMediantime(Date mediantime) {
		this.mediantime = mediantime;
	}

	public double getVerificationprogress() {
		return verificationprogress;
	}

	public void setVerificationprogress(double verificationprogress) {
		this.verificationprogress = verificationprogress;
	}

	public String getChainwork() {
		return chainwork;
	}

	public void setChainwork(String chainwork) {
		this.chainwork = chainwork;
	}

	public String getPruned() {
		return pruned;
	}

	public void setPruned(String pruned) {
		this.pruned = pruned;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
