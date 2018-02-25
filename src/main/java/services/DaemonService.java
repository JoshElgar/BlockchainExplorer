package services;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import entities.Block;
import entities.Transaction;

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
			// blockInfo =
			// "{\"hash\":\"0000000000000000005be247a670caadf09f613b9e18aa3a79de952dc39d4797\",\"confirmations\":646,\"strippedsize\":983241,\"size\":1043590,\"weight\":3993313,\"height\":509184,\"version\":536870912,\"versionHex\":\"20000000\",\"merkleroot\":\"6a81db52b906fd5629dd6fb4795951ba58f32c7a7ed7943ab528d615015ad8d1\",\"tx\":[{\"txid\":\"772d6474c1f163e360a35d1b52750dba5f14728769875f4b1a66dbdbb5b21755\",\"hash\":\"128f71446155f8c8fb79b4909a87906a93295ecc191e5721134642d73333a2fe\",\"version\":1,\"size\":289,\"vsize\":262,\"locktime\":0,\"vin\":[{\"coinbase\":\"0300c507fabe6d6d29d6852667515d60496fb4860f7b3437a96234b008f0b3018ce5221cccda2093010000000000000001650800132f583880000056892f736c7573682f\",\"sequence\":0}],\"vout\":[{\"value\":12.98461985,\"n\":0,\"scriptPubKey\":{\"asm\":\"OP_DUP
			// OP_HASH160 7c154ed1dc59609e3d26abb2df2ea3d587cd8c41 OP_EQUALVERIFY
			// OP_CHECKSIG\",\"hex\":\"76a9147c154ed1dc59609e3d26abb2df2ea3d587cd8c4188ac\",\"reqSigs\":1,\"type\":\"pubkeyhash\",\"addresses\":[\"1CK6KHY6MHgYvmRQ4PAafKYDrg1ejbH1cE\"]}},{\"value\":0.00000000,\"n\":1,\"scriptPubKey\":{\"asm\":\"OP_RETURN
			// 52534b424c4f434b3a79724bb415896b3791ac1cb2c72c5881a31d5d430ba5523761364575ca4f3b2a\",\"hex\":\"6a4c2952534b424c4f434b3a79724bb415896b3791ac1cb2c72c5881a31d5d430ba5523761364575ca4f3b2a\",\"type\":\"nulldata\"}},{\"value\":0.00000000,\"n\":2,\"scriptPubKey\":{\"asm\":\"OP_RETURN
			// aa21a9ed99993db633621199e81c58278a47559f12247a6f4bdb85516a395a284f4831d9\",\"hex\":\"6a24aa21a9ed99993db633621199e81c58278a47559f12247a6f4bdb85516a395a284f4831d9\",\"type\":\"nulldata\"}}],\"hex\":\"010000000001010000000000000000000000000000000000000000000000000000000000000000ffffffff440300c507fabe6d6d29d6852667515d60496fb4860f7b3437a96234b008f0b3018ce5221cccda2093010000000000000001650800132f583880000056892f736c7573682f000000000321f5644d000000001976a9147c154ed1dc59609e3d26abb2df2ea3d587cd8c4188ac00000000000000002c6a4c2952534b424c4f434b3a79724bb415896b3791ac1cb2c72c5881a31d5d430ba5523761364575ca4f3b2a0000000000000000266a24aa21a9ed99993db633621199e81c58278a47559f12247a6f4bdb85516a395a284f4831d90120000000000000000000000000000000000000000000000000000000000000000000000000\"}],\"time\":1518629592,\"mediantime\":1518627378,\"nonce\":1356686654,\"bits\":\"1761e9f8\",\"difficulty\":2874674234415.941,\"chainwork\":\"000000000000000000000000000000000000000001170cb82a4a0a389b90d9c8\",\"previousblockhash\":\"0000000000000000005c722a01bb0cc4c9139c8b0f1fadf265f87428333a7354\",\"nextblockhash\":\"00000000000000000060fb1316cf1765956d04e95dde1c30c633a72582f2d4d3\"}";

			block = new ObjectMapper().readValue(blockInfo, Block.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return block;

	}

	public Transaction getTxByTxid(String Txid) {

		Transaction tx = null;

		try {

			CloseableHttpResponse txResponse;
			CloseableHttpClient httpclient = HttpClients.createDefault();

			// get next block
			HttpGet httpgetTx = new HttpGet("http://localhost:8332/rest/tx/" + Txid + ".json");
			txResponse = httpclient.execute(httpgetTx);
			String txInfo = EntityUtils.toString(txResponse.getEntity());
			tx = new ObjectMapper().readValue(txInfo, Transaction.class);
		} catch (JsonParseException e) {
			System.out.println("Invalid transaction, could not be retrieved.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tx;

	}

}
