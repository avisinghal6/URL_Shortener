package io.intellecttitans.springbootbackend.configurations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import io.intellecttitans.springbootbackend.services.CustomOAuth2UserService;
import io.intellecttitans.springbootbackend.utils.CustomOAuth2User;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
	@Autowired
	private UserTable userTable;

	protected void configure(HttpSecurity http) throws Exception {
    	// @formatter:off
        http
        //can remove csrf disable once we connect with frontend, its needed for verifying with postman.
        	.csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST,"/api/longurl/**")
            .permitAll()
            .antMatchers("/api/**")
            .permitAll()
            .anyRequest().authenticated()
            .and().oauth2Login()
            .userInfoEndpoint()
            .userService(oauthUserService).and()
            .successHandler(new SuccessHandler());
        // @formatter:on
    }
	
	
	public class SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	    @Override
	    public void onAuthenticationSuccess(
	            HttpServletRequest request,
	            HttpServletResponse response,
	            Authentication authentication
	    ) throws ServletException, IOException {
	    	Date currentDate = new Date();
	    	CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
	    	String email=oauthUser.getEmail();
	    	String name=oauthUser.getName();
	    	
	    	List<String> subFamily = new ArrayList<>();
			subFamily.add("name");
			subFamily.add("List_of_Urls");
			subFamily.add("created");
			
			List<String> value = new ArrayList<>();
			value.add(name);
			value.add("");
			value.add(currentDate.toString());
			
			if(userTable.rowExists(email)) {
				System.out.println("User Exists!!");
			}else {
				if(!userTable.writeRow(value, subFamily, email)) {
					System.err.println("Error writing to user table");
				}
			
			}
	    	//To redirect to original URL.
	        super.onAuthenticationSuccess(request, response, authentication);
	    }
	}
	
	@Autowired
    private CustomOAuth2UserService oauthUserService;
     
 
}