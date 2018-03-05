package entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction implements Serializable {

	private static final long serialVersionUID = -3009157732242241606L;

	@Id
	@JsonProperty("hash")
	@Column(name = "hash", unique = true)
	private String hash;

	@JsonProperty("blockhash")
	@Column(name = "blockhash")
	private String blockhash;

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
	public List<TxVin> txVin;

	@JsonProperty("vout")
	@JsonDeserialize(using = voutDeserialiser.class)
	public List<TxVout> txVout;

	protected Transaction() {

	}

	public Transaction(String hash, String blockhash, String txid, int bytesize, int version, List<TxVin> txVin,
			List<TxVout> txVout) {
		this.hash = hash;
		this.blockhash = blockhash;
		this.txid = txid;
		this.bytesize = bytesize;
		this.version = version;
		this.txVin = txVin;
		this.txVout = txVout;
	}

	public Transaction(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return String.format("@Transaction[hash=%s, blockhash=%s, txid=%s, bytesize=%d, version=%d]", hash, blockhash,
				txid, bytesize, version);
	}

	public void setBlockHash(String blockhash) {
		this.blockhash = blockhash;
	}

	public String getHash() {
		return hash;
	}

	public String getBlockHash() {
		return blockhash;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
