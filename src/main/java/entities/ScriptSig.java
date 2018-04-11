package entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScriptSig implements Serializable {

	private static final long serialVersionUID = -193893554131028190L;

	public ScriptSig() {

	}

	@JsonProperty("asm")
	private String asm;

	@JsonProperty("hex")
	private String hex;

}
