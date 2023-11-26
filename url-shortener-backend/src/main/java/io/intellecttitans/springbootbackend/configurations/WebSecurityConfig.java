package io.intellecttitans.springbootbackend.configurations;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import io.intellecttitans.springbootbackend.CustomOAuth2User;
import io.intellecttitans.springbootbackend.services.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
     
	protected void configure(HttpSecurity http) throws Exception {
    	// @formatter:off
        http
            .authorizeRequests()
            .antMatchers("/hello")
            .permitAll().anyRequest().authenticated()
            .and().oauth2Login()
            .userInfoEndpoint()
            .userService(oauthUserService).and()
            .successHandler(new AuthenticationSuccessHandler() {
                @Override
                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                        Authentication authentication) throws IOException, ServletException {
         
                    CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
         
                    System.out.println(oauthUser.getEmail());
         
                    response.sendRedirect("/hello");
                }
            });
        // @formatter:on
    }
	
	@Autowired
    private CustomOAuth2UserService oauthUserService;
     
 
}