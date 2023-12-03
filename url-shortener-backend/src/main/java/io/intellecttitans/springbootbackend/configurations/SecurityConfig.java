package io.intellecttitans.springbootbackend.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityConfig {
//	public void hello() {
//		System.out.println("hi");
//	}

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String issuerUri; // This should be the Google issuer URI

    
    public JwtDecoder jwtDecoder() {
    	NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(issuerUri)
                .build();
    	return jwtDecoder;
    }
}