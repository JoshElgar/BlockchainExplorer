package repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import entities.Block;

@Repository
public interface BlockUserRepository extends MongoRepository<Block, String> {

	List<Block> findTop5ByOrderByHeightDesc();

	Block findFirstByHash(String hash);

	Block findFirstByOrderByHeightDesc();

	@Query(value = "{'transactions.txid' : ?0}")
	Block findBlockContainingTxWithTxid(String txid);

	@Query(value = "{'transactions.hash' : ?0}")
	Block findBlockContainingTxWithHash(String transactionHash);

	long count();

}
