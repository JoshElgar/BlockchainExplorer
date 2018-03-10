package entities;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScriptPubKey implements Serializable {

	private static final long serialVersionUID = -5332448207449787645L;

	protected ScriptPubKey() {
	}

	@JsonProperty("asm")
	private String asm;

	@JsonProperty("hex")
	private String hex;

	@JsonProperty("reqsigs")
	private int reqsigs;

	@JsonProperty("type")
	private String type;

	@JsonProperty("addresses")
	private List<String> addresses;

	public String getAsm() {
		return asm;
	}

	public void setAsm(String asm) {
		this.asm = asm;
	}

	public String getHex() {
		return hex;
	}

	public void setHex(String hex) {
		this.hex = hex;
	}

	public int getReqsigs() {
		return reqsigs;
	}

	public void setReqsigs(int reqsigs) {
		this.reqsigs = reqsigs;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}

}
