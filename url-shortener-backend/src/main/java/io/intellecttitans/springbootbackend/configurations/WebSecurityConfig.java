package io.intellecttitans.springbootbackend.configurations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            .authorizeRequests()
            .antMatchers("/hello")
            .permitAll().anyRequest().authenticated()
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
	        // Your custom actions go here

	        // Continue with the default behavior (redirect to the original requested URL)
	    	CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
	    	// 
	    	String email=oauthUser.getEmail();
	    	String name=oauthUser.getName();
	    	
	    	List<String> subFamily = new ArrayList<>();
			subFamily.add("email");
			subFamily.add("created");
			
			List<String> value = new ArrayList<>();
			value.add(email);
			value.add("5_Nov");
			
			if(userTable.rowExists(name)) {
				System.out.println("User Exists!!");
			}else {
				userTable.writeRow(value, subFamily, name);
			
			}
	    	System.out.println(oauthUser.getEmail());
	    	System.out.println("once");
	        super.onAuthenticationSuccess(request, response, authentication);
	    }
	}
	
	@Autowired
    private CustomOAuth2UserService oauthUserService;
     
 
}