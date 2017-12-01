package home;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity //indicates Block is entity
@Table(name = "transaction") //indicates primary table name for entity Block
public class Transaction implements Serializable {

	private static final long serialVersionUID = -3009157732242241606L;
	
	
	@Id
	private String hash;
	
	@Column(name="blockhash")
	private String blockhash;
	
	@Column(name = "txid")
	private String txid;

	@Column(name="bytesize")
	private int bytesize;
	
	@Column(name="version")
	private int version;

	
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getBlockhash() {
		return blockhash;
	}

	public void setBlockhash(String blockhash) {
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

	protected Transaction() {
		
	}
	
	public Transaction(String hash, String blockhash, String txid, int bytesize, int version) {
		this.hash = hash;
		this.blockhash = blockhash;
		this.txid = txid;
		this.bytesize = bytesize;
		this.version = version;
	}
	
	@Override
	public String toString() {
		return String.format("@Transaction[hash=%s, blockhash=%s, txid=%s, bytesize=%d, version=%d]", hash, blockhash, txid, bytesize, version);
	}
	
	
	
	
	
}
