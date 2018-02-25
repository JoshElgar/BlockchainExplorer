package entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "txvin")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxVin implements Serializable {

	private static final long serialVersionUID = 3510862926366378424L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("serialid")
	@Column(name = "serialid")
	public int serialid;

	@JsonProperty("txhash")
	@Column(name = "txhash")
	public String txhash;

	@JsonProperty("coinbase")
	@Column(name = "coinbase")
	public String coinbase;

	@JsonProperty("txid")
	@Column(name = "txid")
	public String txid;

	@JsonProperty("sequence")
	@Column(name = "vinsequence")
	public int vinsequence;

	@JsonProperty("vout")
	@Column(name = "vout")
	public int vout;

	protected TxVin() {
	}

	public TxVin(String coinbase, int vinsequence) {
		super();
		this.coinbase = coinbase;
		this.vinsequence = vinsequence;
	}

	public TxVin(String txid, int vout, int vinsequence) {
		super();
		this.txid = txid;
		this.vout = vout;
		this.vinsequence = vinsequence;
	}

	public boolean isCoinbase() {
		return coinbase == null ? false : true;
	}

	public String getCoinbase() {
		return coinbase;
	}

	public String getTxHash() {
		return txhash;
	}

	public int getSequence() {
		return vinsequence;
	}

	public void setCoinbase(String coinbase) {
		this.coinbase = coinbase;
	}

	public void setTxHash(String txHash) {
		this.txhash = txHash;
	}

	public void setSequence(int sequence) {
		this.vinsequence = sequence;
	}

}
