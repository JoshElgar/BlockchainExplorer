package entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
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

	@Transient
	@JsonProperty("vin")
	@JsonDeserialize(using = vinDeserialiser.class)
	public List<TxVin> txVin;

	@Transient
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

	public Transaction(String hash, int serialId) {
		this.hash = hash;
		this.serialid = serialId;
	}

	@Override
	public String toString() {
		return String.format("@Transaction[hash=%s, blockhash=%s, txid=%s, bytesize=%d, version=%d]", hash, blockhash,
				txid, bytesize, version);
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
