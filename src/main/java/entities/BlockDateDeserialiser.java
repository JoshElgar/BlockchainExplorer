package entities;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class BlockDateDeserialiser extends StdDeserializer<Date> {

	private static final long serialVersionUID = 1L;

	protected BlockDateDeserialiser(Class<?> vc) {
		super(vc);
	}

	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		JsonNode jsonNode = jp.getCodec().readTree(jp);

		String s = jsonNode.get("time").asText();

		Date d = Date.from(Instant.ofEpochMilli(Long.parseLong(s) * 1000L));
		return d;
	}

}
