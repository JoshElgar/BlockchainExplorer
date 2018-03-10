package entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction implements Serializable {

	private static final long serialVersionUID = -3009157732242241606L;

	@org.springframework.data.annotation.Id
	@JsonProperty("hash")
	@Column(name = "hash", unique = true)
	private String hash;

	private String blockHash;

	@JsonProperty("txid")
	@Column(name = "txid")
	private String txid;

	@JsonProperty("vsize")
	@Column(name = "bytesize")
	private int bytesize;

	@JsonProperty("version")
	@Column(name = "version")
	private int version;

	@JsonProperty("vin")
	@JsonDeserialize(using = vinDeserialiser.class)
	private List<TxVin> txVin;

	@JsonProperty("vout")
	@JsonDeserialize(using = voutDeserialiser.class)
	private List<TxVout> txVout;

	// @JsonDeserialize(using = scriptPubKeyDeserialiser.class)
	@JsonProperty("scriptPubKey")
	private ScriptPubKey scriptPubKey;

	protected Transaction() {

	}

	public Transaction(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return String.format("@Transaction[hash=%s, txid=%s, bytesize=%d, version=%d]", hash, txid, bytesize, version);
	}

	public String getHash() {
		return hash;
	}

	public String getTxid() {
		return txid;
	}

	public int getByteSize() {
		return bytesize;
	}

	public int getVersion() {
		return version;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public List<TxVin> getTxVin() {
		return txVin;
	}

	public void setTxVin(List<TxVin> txVin) {
		this.txVin = txVin;
	}

	public List<TxVout> getTxVout() {
		return txVout;
	}

	public void setTxVout(List<TxVout> txVout) {
		this.txVout = txVout;
	}

	public ScriptPubKey getScriptPubKey() {
		return scriptPubKey;
	}

	public void setScriptPubKey(ScriptPubKey scriptPubKey) {
		this.scriptPubKey = scriptPubKey;
	}

}
