package entities;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class scriptPubKeyDeserialiser extends StdDeserializer<ScriptPubKey> {

	public scriptPubKeyDeserialiser() {
		this(null);
	}

	protected scriptPubKeyDeserialiser(Class<?> vc) {
		super(vc);
	}

	@Override
	public ScriptPubKey deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		JsonNode jsonNode = p.getCodec().readTree(p);

		ScriptPubKey spKey = new ScriptPubKey();

		return null;
	}

}
