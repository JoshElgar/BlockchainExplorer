package home;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface BlockRepository extends CrudRepository<Block, String> {
	List<Block> findByHash(String hash);
}
