package entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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

	@JsonProperty("confirmations")
	@Column(name = "confirmations")
	private int confirmations;

	@JsonDeserialize(using = DateDeserialiser.class)
	@JsonProperty("time")
	@Column(name = "time")
	private Timestamp time;

	protected Transaction() {

	}

	public Transaction(String hash, String blockhash, String txid, int bytesize, int version, int confirmations,
			Timestamp time) {
		this.hash = hash;
		this.blockhash = blockhash;
		this.txid = txid;
		this.bytesize = bytesize;
		this.version = version;
		this.confirmations = confirmations;
		this.time = time;
	}

	public Transaction(String hash, int serialId) {
		this.hash = hash;
		this.serialid = serialId;
	}

	@Override
	public String toString() {
		return String.format(
				"@Transaction[hash=%s, blockhash=%s, txid=%s, bytesize=%d, version=%d, confirmations=%d, time=%s]",
				hash, blockhash, txid, bytesize, version, confirmations, time.toString());
	}

	public void setBlockHash(String blockhash) {
		this.blockhash = blockhash;
	}

	public int getSerialid() {
		return serialid;
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

	public int getConfirmations() {
		return confirmations;
	}

	public Timestamp getTime() {
		return time;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
