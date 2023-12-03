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


public class CustomJwtDecoder extends OncePerRequestFilter {

	
	SecurityConfig securityConfig;
	
	GoogleUserInfoService googleservice;
	
	@Autowired
    public CustomJwtDecoder(GoogleUserInfoService googleservice,SecurityConfig securityConfig ) {
        this.googleservice = googleservice;
        this.securityConfig=securityConfig;
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
                	System.out.println(authElements[1]);
                	Jwt jwt=jwtdecoder.decode(authElements[1]);
                	UserDetails data=googleservice.getUserDetails(jwt);
                	
//                	System.out.println(data);
                	if (data != null) {
                        Authentication authentication = new UsernamePasswordAuthenticationToken(data,null);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
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