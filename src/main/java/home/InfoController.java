package home;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@Controller
public class InfoController {

	@RequestMapping("/info")
	public String info(Model model) {
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpgetChainInfo = new HttpGet("http://localhost:8332/rest/chaininfo.json");
		HttpGet httpgetBlock = new HttpGet("http://localhost:8332/rest/block/0000000000000000000e9c40575a5e343f1166954d92c82ccac77d31e4d3f74f.json");
		
		
		CloseableHttpResponse resp1, resp2;
		
		
		try {
			
			System.out.println("Requesting chain info.");
			resp1 = httpclient.execute(httpgetChainInfo);
			
			
			System.out.println("Requesting specific block info.");
			resp2 = httpclient.execute(httpgetBlock);
			
			
			String chainInfo = EntityUtils.toString(resp1.getEntity());
			String blockInfo = EntityUtils.toString(resp2.getEntity());
			
			GsonBuilder gsonBuilder = new GsonBuilder();
			
			// Register an adapter to manage the date types as long values 
			gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() { 
			
				@Override
			   public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				   
					String s = json.getAsJsonPrimitive().getAsString();
					
				    Date d = Date.from(Instant.ofEpochMilli(Long.parseLong(s)*1000L));
				    return d;
					
				}
			});
			
			Gson gson = gsonBuilder.create();
			
			Block generatedBlock = gson.fromJson(blockInfo, Block.class);
			System.out.println("Generated block: " + generatedBlock.toString());
			
			resp1.close();
			resp2.close();
			
			//System.out.println(response);
			
			model.addAttribute("chainInfo", chainInfo);
			model.addAttribute("blockInfo", blockInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return "info";
	}
}
