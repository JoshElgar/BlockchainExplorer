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
@Table(name = "txvout")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TxVout implements Serializable {

	private static final long serialVersionUID = -2969447956057942695L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("serialid")
	@Column(name = "serialid")
	private int serialid;

	@JsonProperty("txhash")
	@Column(name = "txhash")
	private String txhash;

	@JsonProperty("value")
	@Column(name = "value")
	private long value;

	protected TxVout() {
	}

	public TxVout(long value) {
		super();
		this.value = value;
	}

	public int getSerialid() {
		return serialid;
	}

	public void setSerialid(int serialid) {
		this.serialid = serialid;
	}

	public String getTxHash() {
		return txhash;
	}

	public void setTxHash(String txHash) {
		this.txhash = txHash;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}
