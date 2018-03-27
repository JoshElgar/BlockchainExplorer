package home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import repositories.BlockUserRepository;

@EnableMongoRepositories("repositories")
@EntityScan("entities")
@SpringBootApplication(scanBasePackages = { "home", "controllers", "services", "entities", "repositories", "configs" })
@EnableAutoConfiguration
public class Application {

	@Autowired
	BlockUserRepository mongoRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			/*
			 * logger.info("Let's inspect the beans provided by Spring Boot:");
			 * 
			 * String[] beanNames = ctx.getBeanDefinitionNames(); Arrays.sort(beanNames);
			 * 
			 * for (String beanName : beanNames) { logger.info(beanName); }
			 */

		};
	}

}
