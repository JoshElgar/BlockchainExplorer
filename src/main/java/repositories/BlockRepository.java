package repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import entities.Block;

@Repository
public interface BlockRepository extends CrudRepository<Block, String> {

	List<Block> findByHash(String hash);

	long count();
}
