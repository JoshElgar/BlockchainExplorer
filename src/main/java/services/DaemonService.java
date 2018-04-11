package services;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import entities.Block;
import entities.ChainInfo;
import entities.Transaction;

@Service
public class DaemonService {

	private static final Logger logger = LogManager.getLogger(DaemonService.class);

	public ChainInfo getChainInfo() {

		ChainInfo chainInfo = new ChainInfo();

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse chainResponse;
		HttpGet httpChainInfo = new HttpGet("http://localhost:8332/rest/chaininfo.json");
		try {
			chainResponse = httpclient.execute(httpChainInfo);
			String chainInfoString = EntityUtils.toString(chainResponse.getEntity());

			chainInfo = new ObjectMapper().readValue(chainInfoString, ChainInfo.class);

		} catch (HttpHostConnectException e) {
			logger.info("Could not connect to daemon : probably offline");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return chainInfo;

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

	public Transaction getTxByTxid(String txid) {

		Transaction tx = null;

		try {

			CloseableHttpResponse txResponse;
			CloseableHttpClient httpclient = HttpClients.createDefault();

			// get next block
			HttpGet httpgetTx = new HttpGet("http://localhost:8332/rest/tx/" + txid + ".json");
			txResponse = httpclient.execute(httpgetTx);
			String txInfo = EntityUtils.toString(txResponse.getEntity());
			tx = new ObjectMapper().readValue(txInfo, Transaction.class);
		} catch (JsonParseException e) {
			logger.info("Invalid transaction, could not be retrieved.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tx;

	}

}
