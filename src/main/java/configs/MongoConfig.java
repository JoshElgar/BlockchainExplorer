package configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

	public @Bean MongoDbFactory mongoDbFactory() {
		return new SimpleMongoDbFactory(mongoClient(), "BlockExplorerDB");
	}

	public @Bean MongoClient mongoClient() {
		return new MongoClient("localhost", 27017);
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mt = new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
		return mt;
	}

	@Override
	protected String getDatabaseName() {
		return "BlockExplorerDB";
	}

	@Override
	public Mongo mongo() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
