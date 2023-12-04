package io.intellecttitans.springbootbackend.configurations;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.intellecttitans.springbootbackend.services.GoogleUserInfoService;
import io.intellecttitans.springbootbackend.utils.UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class CustomJwtDecoder extends OncePerRequestFilter {

	
	SecurityConfig securityConfig;
	
	GoogleUserInfoService googleservice;
	
	UserTable userTable;
	
	
	@Autowired
    public CustomJwtDecoder(GoogleUserInfoService googleservice,SecurityConfig securityConfig, UserTable userTable ) {
        this.googleservice = googleservice;
        this.securityConfig=securityConfig;
        this.userTable=userTable;
    }
	
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException, JWTDecodeException, IllegalArgumentException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (header != null) {
            String[] authElements = header.split(" ");

            if(authElements.length != 2)
                throw new JWTVerificationException("INVALID TOKEN");

            if ("Bearer".equals(authElements[0])) {
                try {

                	JwtDecoder jwtdecoder = this.securityConfig.jwtDecoder();
                	
                	Jwt jwt=jwtdecoder.decode(authElements[1]);
                	System.out.println(jwt);
                	UserDetails data=googleservice.getUserDetails(jwt);
                	
                	System.out.println(data);
                	System.out.println(data.getEmail());
                	if (data != null) {
                		Date currentDate = new Date();
                        Authentication authentication = new UsernamePasswordAuthenticationToken(data,null,Collections.emptyList());
                        
//                        System.out.println(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
//                        System.out.println(auth.getName());
//                        System.out.println(auth.isAuthenticated());
//                        System.out.println(auth.getPrincipal().toString());
                        List<String> subFamily = new ArrayList<>();
            			subFamily.add("name");
            			subFamily.add("List_of_Urls");
            			subFamily.add("created");
            			String name=data.getName();
            			String email=data.getEmail();
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
                    }
               
                }
                catch (JWTDecodeException e) {
                    System.err.println("Error");
                    return;
                }
                catch (TokenExpiredException e) {
                	System.err.println("Error");
                    return;
                }
                catch (RuntimeException e) {
                    SecurityContextHolder.clearContext();
                    throw e;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

}