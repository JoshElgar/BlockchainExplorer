package repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import entities.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, String> {
	List<Transaction> findByHash(String hash);
}
