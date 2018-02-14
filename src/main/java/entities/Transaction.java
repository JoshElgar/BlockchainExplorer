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

@Entity // indicates Block is entity
@Table(name = "transaction")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction implements Serializable {

	private static final long serialVersionUID = -3009157732242241606L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("serialid")
	@Column(name = "serialid")
	private int serialid;

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

	protected Transaction() {

	}

	public Transaction(String hash, String blockhash, String txid, int bytesize, int version) {
		this.hash = hash;
		this.blockhash = blockhash;
		this.txid = txid;
		this.bytesize = bytesize;
		this.version = version;
	}

	public Transaction(String hash, int serialId) {
		this.hash = hash;
		this.serialid = serialId;
	}

	@Override
	public String toString() {
		return String.format("@Transaction[hash=%s, blockhash=%s, txid=%s, bytesize=%d, version=%d]", hash, blockhash,
				txid, bytesize, version);
	}

	public int getSerialid() {
		return serialid;
	}

	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getBlockHash() {
		return blockhash;
	}

	public void setBlockHash(String blockhash) {
		this.blockhash = blockhash;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public int getBytesize() {
		return bytesize;
	}

	public void setBytesize(int bytesize) {
		this.bytesize = bytesize;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
