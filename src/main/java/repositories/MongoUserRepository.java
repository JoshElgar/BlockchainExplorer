package repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import entities.Block;

public interface MongoUserRepository extends MongoRepository<Block, String> {

	Block findFirstByHash(String hash);

	Block findFirstByOrderByHeightDesc();

	long count();

}
