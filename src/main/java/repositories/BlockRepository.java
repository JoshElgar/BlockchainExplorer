package repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import entities.Block;

@Repository
public interface BlockRepository extends CrudRepository<Block, String> {

	Block findFirstByHash(String hash);

	Block findFirstByOrderByHeightDesc();

	long count();
}
