package repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import entities.Transaction;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
	List<Transaction> findByHash(String hash);

	Transaction findFirstByHash(String transactionHash);
}
