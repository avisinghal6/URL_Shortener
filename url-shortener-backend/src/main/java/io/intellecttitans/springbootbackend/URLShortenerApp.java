package io.intellecttitans.springbootbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class URLShortenerApp extends WebSecurityConfigurerAdapter{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(URLShortenerApp.class, args);
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
    	// @formatter:off
        http
            .authorizeRequests()
            .antMatchers("/hello")
            .permitAll().anyRequest().authenticated()
            .and().oauth2Login();
        // @formatter:on
    }

}
