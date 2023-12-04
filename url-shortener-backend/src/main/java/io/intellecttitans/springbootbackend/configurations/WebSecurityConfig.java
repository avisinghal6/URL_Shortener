package io.intellecttitans.springbootbackend.configurations;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.intellecttitans.springbootbackend.services.GoogleUserInfoService;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity

public class WebSecurityConfig implements WebMvcConfigurer{
	private GoogleUserInfoService googleservice;
	
	@Autowired 
	SecurityConfig securityConfig;
	
	@Autowired
	UserTable userTable;
	
	@Autowired
    public WebSecurityConfig(GoogleUserInfoService googleservice) {
        this.googleservice = googleservice;
    }
	
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(new CustomJwtDecoder(googleservice,securityConfig,userTable), BasicAuthenticationFilter.class)
				
				.authorizeHttpRequests((requests) -> requests
						.anyRequest()
						.authenticated()
						)
				;
			
		return http.build();
	}
	
//	@Override
//    public void addCorsMappings(CorsRegistry registry) {
//		
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("ACCESS_CONTROL_ALLOW_ORIGIN")
//                .allowCredentials(true);
//    }
	
//	@Bean
//    public FilterRegistrationBean corsFilter() {
//		System.out.println("inside filter chain");
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("http://localhost:3000");
//        config.setAllowedHeaders(Arrays.asList(
//                HttpHeaders.AUTHORIZATION,
//                HttpHeaders.CONTENT_TYPE,
//                HttpHeaders.ACCEPT,
//                HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN
//        ));
//
//        config.setAllowedMethods(Arrays.asList(
//                HttpMethod.GET.name(),
//                HttpMethod.POST.name(),
//                HttpMethod.PUT.name(),
//                HttpMethod.DELETE.name(),
//                HttpMethod.OPTIONS.name()
//        ));
//
//        config.setMaxAge(3600L);
//        source.registerCorsConfiguration("/**", config);
//        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//        bean.setOrder(-102);
//        return bean;
//    }
	
}
