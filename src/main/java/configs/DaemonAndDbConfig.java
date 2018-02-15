package configs;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import services.DaemonService;
import services.DbService;

@Configuration
public class DaemonAndDbConfig {

	@Bean
	public DataSource postgresDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/explorerDB");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres");

		return dataSource;
	}

	@Bean
	public DbService dbService() {
		return new DbService();
	}

	@Bean
	public DaemonService daemonService() {
		return new DaemonService();
	}

}
