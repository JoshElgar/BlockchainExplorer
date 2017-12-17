package entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity // indicates Block is entity
@Table(name = "block") // indicates primary table name for entity Block
public class Block implements Serializable {

	private static final long serialVersionUID = -3009157732242241606L;

	@Id // primary key
	// @GeneratedValue(strategy = GenerationType.AUTO) //generation strategy for
	// primary key value
	private String hash;

	@Column(name = "height") // mapped column name in table
	private int height;

	@Column(name = "confirmations")
	private int confirmations;

	@Column(name = "time")
	private Timestamp time;

	// @SerializedName("strippedsize")
	@Column(name = "bytesize")
	private int bytesize;

	// @SerializedName("version")
	@Column(name = "blockversion")
	private int blockversion;

	@Column(name = "nonce")
	private long nonce;

	@Column(name = "merkleroot")
	private String merkleroot;

	// @SerializedName("previousblockhash")
	@Column(name = "prevblockhash")
	private String prevblockhash;

	// @SerializedName("nextblockhash")
	@Column(name = "nextblockhash")
	private String nextblockhash;

	@Column(name = "bits")
	private String bits;

	@Column(name = "difficulty")
	private float difficulty;

	@Transient
	@JsonInclude()
	// @SerializedName("tx")
	private List<Transaction> transactions;

	// 2 constructors: protected used by Spring JPA, public for creating instances
	protected Block() {

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

	public String getPrevBlockHash() {
		return this.prevblockhash;
	}

	public List<Transaction> getTransactions() {
		return this.transactions;
	}

	public int getHeight() {
		return this.height;
	}

	public Timestamp getTime() {
		return this.time;
	}
}
