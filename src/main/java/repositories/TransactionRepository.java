package repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import entities.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
	List<Transaction> findByHash(String hash);

	Transaction findFirstByTxid(String txid);

	Transaction findFirstByHash(String transactionHash);
}
