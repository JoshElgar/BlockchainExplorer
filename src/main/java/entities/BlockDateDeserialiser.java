package entities;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class BlockDateDeserialiser implements JsonDeserializer<Date> {
	
	@Override
	   public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		   
			String s = json.getAsJsonPrimitive().getAsString();
			
		    Date d = Date.from(Instant.ofEpochMilli(Long.parseLong(s)*1000L));
		    return d;
			
		}
}
