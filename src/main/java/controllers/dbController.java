package controllers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entities.Block;
import entities.BlockDateDeserialiser;
import entities.Transaction;
import repositories.BlockRepository;
import repositories.TransactionRepository;

@Controller
public class dbController {

	@Autowired
	BlockRepository blockRepo;

	@Autowired
	TransactionRepository transactionRepo;

	@RequestMapping(value = "/testportal")
	public String servePage(Model m) {
		System.out.println("Serving testportal");
		return "dbPortal";
	}
	
	@RequestMapping(value = "/testportal/triggersync")
	public void triggerSync() {
		sync();
	}

	public void sync() {
		// get json blocks (+ transactions)
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpgetBlock = new HttpGet(
				"http://localhost:8332/rest/block/0000000000000000000e9c40575a5e343f1166954d92c82ccac77d31e4d3f74f.json");

		CloseableHttpResponse resp;

		try {
			resp = httpclient.execute(httpgetBlock);
			String blockInfo = EntityUtils.toString(resp.getEntity());

			// Register an adapter to manage the date types as long values
			GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Date.class, new BlockDateDeserialiser());

			Gson gson = gsonBuilder.create();

			Block generatedBlock = gson.fromJson(blockInfo, Block.class);
			
			System.out.println("Saving block with hash:" + generatedBlock.getHash());
			
			blockRepo.save(generatedBlock);
			
			System.out.println("Saved");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// deserialise into Block
		// save block
	}

	@RequestMapping(value = "/testportal/save", method = RequestMethod.GET)
	public String saveWithoutTransactions(Model model, @RequestParam("hash") String hash,
			@RequestParam("height") int height, @RequestParam("confirmations") int confirmations,
			@RequestParam("transactions") int numtransactions, @RequestParam("time") long time,
			@RequestParam("bytesize") int bytesize, @RequestParam("blockversion") int blockversion,
			@RequestParam("nonce") int nonce, @RequestParam("merkleroot") String merkleroot,
			@RequestParam("prevblockhash") String prevblockhash, @RequestParam("nextblockhash") String nextblockhash,
			@RequestParam("bits") String bits, @RequestParam("difficultyrating") float difficultyrating) {

		Instant instantTime = Instant.ofEpochMilli(time * 1000L);
		Timestamp ts = instantTime != null ? new Timestamp(instantTime.toEpochMilli()) : null;

		// List<Transaction> transactions = new ArrayList<Transaction>();
		/*
		 * Save Block
		 */
		List<Transaction> emptyTransactions = new ArrayList<Transaction>();
		blockRepo.save(new Block(hash, height, confirmations, numtransactions, ts, bytesize, blockversion, nonce,
				merkleroot, prevblockhash, nextblockhash, bits, difficultyrating, emptyTransactions));

		model.addAttribute("savedBlockHash", hash);
		return "saved";
	}

}
