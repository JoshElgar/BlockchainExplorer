package configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/static/**").permitAll() //requests matched against /static/**
				.antMatchers("/", "/home", "/data", "/api/**").authenticated()
				.and()
				.formLogin()	//	form-based authentication is enabled with a custom login page and failure url
					.loginPage("/login").permitAll()
					.defaultSuccessUrl("/home.html")
				.and()
				.logout().logoutSuccessUrl("/login.html");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
	}
}
