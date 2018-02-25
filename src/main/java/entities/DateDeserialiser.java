package entities;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class DateDeserialiser extends StdDeserializer<Timestamp> {

	private static final long serialVersionUID = 189350860793782692L;

	public DateDeserialiser() {
		this(null);
	}

	public DateDeserialiser(Class<?> vc) {
		super(vc);
	}

	@Override
	public Timestamp deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		JsonNode jsonNode = jp.getCodec().readTree(jp);

		String s = jsonNode.asText();

		Instant instantTime = Instant.ofEpochMilli(Long.parseLong(s) * 1000L);
		Timestamp ts = instantTime != null ? new Timestamp(instantTime.toEpochMilli()) : null;

		return ts;
	}

}
