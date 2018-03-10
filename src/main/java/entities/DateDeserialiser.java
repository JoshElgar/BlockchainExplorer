package entities;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class DateDeserialiser extends StdDeserializer<Date> {

	private static final long serialVersionUID = 189350860793782692L;

	public DateDeserialiser() {
		this(null);
	}

	public DateDeserialiser(Class<?> vc) {
		super(vc);
	}

	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		JsonNode jsonNode = jp.getCodec().readTree(jp);

		String s = jsonNode.asText();

		Date d = new Date(Long.parseLong(s) * 1000L);

		return d;
	}

}
