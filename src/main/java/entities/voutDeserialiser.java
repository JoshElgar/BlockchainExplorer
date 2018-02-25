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

public class voutDeserialiser extends StdDeserializer<List<TxVout>> {

	private static final long serialVersionUID = -6855449160082227866L;

	public voutDeserialiser() {
		this(null);
	}

	protected voutDeserialiser(Class<?> vc) {
		super(vc);
	}

	@Override
	public List<TxVout> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		JsonNode jsonNode = p.getCodec().readTree(p);

		List<TxVout> txVouts = new ArrayList<TxVout>();

		Consumer<JsonNode> data = (JsonNode node) -> {

			try {

				txVouts.add(new TxVout(node.findValue("value").asLong()));

			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		jsonNode.forEach(data);

		return txVouts;
	}

}
