package entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Id;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "blocks")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Block implements Serializable {

	private static final long serialVersionUID = -3009157732242241606L;

	@Id // primary key
	private String hash;

	@JsonProperty("height")
	private int height;

	@Transient
	@JsonInclude()
	@JsonProperty("numTx")
	private int numTx;

	@JsonProperty("confirmations")
	private int confirmations;

	// @JsonDeserialize(using = DateDeserialiser.class)
	@JsonProperty("time")
	private Date time;

	@JsonProperty("strippedsize")
	private int bytesize;

	@JsonProperty("version")
	private int blockversion;

	@JsonProperty("nonce")
	private long nonce;

	@JsonProperty("merkleroot")
	private String merkleroot;

	@JsonProperty("previousblockhash")
	private String prevblockhash;

	@JsonProperty("nextblockhash")
	private String nextblockhash;

	@JsonProperty("bits")
	private String bits;

	@JsonProperty("difficulty")
	private float difficulty;

	@Embedded
	@JsonInclude()
	@JsonProperty("tx")
	private List<Transaction> transactions;

	// 2 constructors: protected used by Spring JPA, public for creating instances
	protected Block() {

	}

	public Block(String hash) {
		this.hash = hash;
	}

	public Block(String hash, int height, int numTx, Timestamp time) {
		this.hash = hash;
		this.height = height;
		this.numTx = numTx;
		this.time = time;
	}

	public Block(String hash, int height, int confirmations, Timestamp time, int bytesize, int blockversion, long nonce,
			String merkleroot, String prevblockhash, String nextblockhash, String bits, float difficulty,
			List<Transaction> transactions) {
		this.hash = hash;
		this.height = height;
		this.confirmations = confirmations;
		this.time = time;
		this.bytesize = bytesize;
		this.blockversion = blockversion;
		this.nonce = nonce;
		this.merkleroot = merkleroot;
		this.prevblockhash = prevblockhash;
		this.nextblockhash = nextblockhash;
		this.bits = bits;
		this.difficulty = difficulty;
		this.transactions = transactions;
	}

	@Override
	public String toString() {

		return String.format(
				"@Block[hash=%s, height=%d, confirmations=%d, time=%s, "
						+ "bytesize=%d, blockversion=%d, nonce=%d, merkleroot=%s, prevblockhash=%s, nextblockhash=%s, "
						+ "bits=%s, difficulty=%f], transactionCount: %d",
				hash, height, confirmations, time.toInstant().toString(), bytesize, blockversion, nonce, merkleroot,
				prevblockhash, nextblockhash, bits, difficulty, transactions.size());

	}

	public String getHash() {
		return this.hash;
	}

	public int getHeight() {
		return this.height;
	}

	public int getNumTx() {
		return this.numTx;
	}

	public int getConfirmations() {
		return this.confirmations;
	}

	public Date getTime() {
		return this.time;
	}

	public int getByteSize() {
		return this.bytesize;
	}

	public int getBlockVersion() {
		return this.blockversion;
	}

	public long getNonce() {
		return this.nonce;
	}

	public String getMerkleroot() {
		return this.merkleroot;
	}

	public String getPrevBlockHash() {
		return this.prevblockhash;
	}

	public String getNextBlockHash() {
		return this.nextblockhash;
	}

	public String getBits() {
		return this.bits;
	}

	public float getDifficulty() {
		return this.difficulty;
	}

	public List<Transaction> getTransactions() {
		return this.transactions;
	}
}
