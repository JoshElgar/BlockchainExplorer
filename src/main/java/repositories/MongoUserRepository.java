package repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import entities.Block;

@Repository
public interface MongoUserRepository extends MongoRepository<Block, String> {

	List<Block> findTop5ByOrderByHeightDesc();

	Block findFirstByHash(String hash);

	Block findFirstByOrderByHeightDesc();

	long count();

}
