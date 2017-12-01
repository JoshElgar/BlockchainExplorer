package home;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class dbController {
	
	@Autowired
	BlockRepository blockRepo;
	
	@Autowired
	TransactionRepository transactionRepo;
	
	@RequestMapping(value = "/testportal")
	public String saveWithoutTransactions(Model model, @RequestParam("hash") String hash, @RequestParam("height") int height, @RequestParam("confirmations") int confirmations,
			@RequestParam("transactions") int numtransactions, @RequestParam("time") long time, @RequestParam("bytesize") int bytesize,
			@RequestParam("blockversion") int blockversion, @RequestParam("nonce") int nonce, @RequestParam("merkleroot") String merkleroot,
			@RequestParam("prevblockhash") String prevblockhash, @RequestParam("nextblockhash") String nextblockhash, @RequestParam("bits") String bits,
			@RequestParam("difficultyrating") float difficultyrating) {
		
		Instant instantTime = Instant.ofEpochMilli(time*1000L);
		Timestamp ts = instantTime != null ? new Timestamp(instantTime.toEpochMilli()) : null;
		
		
		//List<Transaction> transactions = new ArrayList<Transaction>();
		/*
		 * Save Block
		 */
		blockRepo.save(new Block(hash, height, confirmations, numtransactions, ts, bytesize, blockversion, nonce, merkleroot, prevblockhash, nextblockhash, bits, difficultyrating));
		
		
		model.addAttribute("savedBlockHash", hash);
		return "saved";
	}
	
	
	

}
