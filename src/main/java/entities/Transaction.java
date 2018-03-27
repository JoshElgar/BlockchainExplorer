package entities;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class Transaction implements Serializable {

	private static final long serialVersionUID = -3009157732242241606L;

	@Id
	@JsonProperty("hash")
	private String hash;

	private String blockHash;

	@JsonProperty("txid")
	private String txid;

	@JsonProperty("vsize")
	private int bytesize;

	@JsonProperty("version")
	private int version;

	@JsonProperty("vin")
	private List<TxVin> txVin;

	@JsonProperty("vout")
	private List<TxVout> txVout;

	public Transaction() {

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

}
