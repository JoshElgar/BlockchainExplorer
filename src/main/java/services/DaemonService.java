package services;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import entities.Block;

@EnableScheduling
@Service
public class DaemonService {

	public Map<String, Object> getChainInfoMap() {

		Map<String, Object> chainMap = new HashMap<String, Object>();

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse chainResponse;
		HttpGet httpChainInfo = new HttpGet("http://localhost:8332/rest/chaininfo.json");
		try {
			chainResponse = httpclient.execute(httpChainInfo);
			String chainInfo = EntityUtils.toString(chainResponse.getEntity());

			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree = mapper.readTree(chainInfo);

			chainMap.put("currentBlocks", tree.get("blocks").asInt());
			chainMap.put("bestblockhash", tree.get("bestblockhash").asText());

		} catch (HttpHostConnectException e) {
			System.out.println("Could not connect to daemon : probably offline");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return chainMap;

	}

	public Block getBlockByHash(String blockHash) {

		Block block = null;

		try {

			CloseableHttpResponse blockResponse;
			CloseableHttpClient httpclient = HttpClients.createDefault();

			// get next block
			HttpGet httpgetBlock = new HttpGet("http://localhost:8332/rest/block/" + blockHash + ".json");
			blockResponse = httpclient.execute(httpgetBlock);
			String blockInfo = EntityUtils.toString(blockResponse.getEntity());

			block = new ObjectMapper().readValue(blockInfo, Block.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return block;

	}

}
