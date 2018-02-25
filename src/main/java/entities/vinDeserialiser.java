package entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class vinDeserialiser extends StdDeserializer<List<TxVin>> {

	private static final long serialVersionUID = 6064612106171845363L;

	public vinDeserialiser() {
		this(null);
	}

	protected vinDeserialiser(Class<?> vc) {
		super(vc);
	}

	@Override
	public List<TxVin> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		JsonNode jsonNode = p.getCodec().readTree(p);

		List<TxVin> txVins = new ArrayList<TxVin>();

		Consumer<JsonNode> data = (JsonNode node) -> {
			try {
				if (node.findValue("coinbase") == null) {

					// add regular txvin to list
					txVins.add(new TxVin(node.findValue("txid").asText(), node.findValue("vout").asInt(),
							node.findValue("sequence").asInt()));

				} else {
					// add new coinbase txvin to list
					txVins.add(new TxVin(node.findValue("coinbase").asText(), node.findValue("sequence").asInt()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		jsonNode.forEach(data);

		return txVins;
	}

}
