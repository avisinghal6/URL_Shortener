package io.intellecttitans.springbootbackend.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.intellecttitans.springbootbackend.utils.UserDetails;

@Service
public class GoogleUserInfoService {

    private final String userInfoUri = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token="; // Google UserInfo endpoint

    public UserDetails getUserDetails(Jwt jwt) {
        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(jwt.getTokenValue());
        String newuserInfoUri=userInfoUri+jwt.getTokenValue();
        System.out.println(jwt.getTokenValue());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDetails> response = new RestTemplate().exchange(
        		newuserInfoUri,
                HttpMethod.GET,
                entity,
                UserDetails.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
        	System.out.println("success");
//        	System.out.println(response.getBody().getName());
//        	System.out.println(response.getBody().getEmail());
            return response.getBody();
        } else {
            // Handle error response from UserInfo endpoint
        	System.err.println("Error");
            return null;
        }
    }
}
