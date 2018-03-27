package entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TxVout implements Serializable {

	private static final long serialVersionUID = -2969447956057942695L;

	@JsonProperty("txhash")
	private String txhash;

	@JsonProperty("value")
	private long value;

	@JsonProperty("scriptPubKey")
	private ScriptPubKey scriptPubKey;

	protected TxVout() {
	}

	public TxVout(long value) {
		super();
		this.value = value;
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

	public ScriptPubKey getScriptPubKey() {
		return scriptPubKey;
	}

	public void setScriptPubKey(ScriptPubKey scriptPubKey) {
		this.scriptPubKey = scriptPubKey;
	}
}
