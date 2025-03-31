package com.dentner.core.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity(debug=false)
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	
	private static final String[] AUTH_WHITELIST = {
			"/api/v1/**",
            "/api/v1/admin/login",
			"/test/**",
    		"/api-docs",
    		"/v3/**", 
    		"/ws/**",
    		"/swagger-ui/**",
    		"/error"
    };
	
	@Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.csrf(AbstractHttpConfigurer::disable)
        	.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                    CorsConfiguration config = new CorsConfiguration();
                    //config.setAllowedOrigins(Collections.singletonList("*"));
                    config.setAllowedOriginPatterns(List.of("http://localhost:4000", "http://localhost:4140", "http://localhost:8080", "http://localhost:3000",
                            "http://192.168.219.42:4000", "http://192.168.219.42:3000", "http://192.168.0.3:4000", "http://115.144.235.93:3000","http://localhost:3001", "http://192.168.219.47:4000",
                            "http://ec2-43-201-195-139.ap-northeast-2.compute.amazonaws.com:4000", "https://192.168.219.42:4000", "https://192.168.219.42:3000",
                            "http://ec2-43-201-195-139.ap-northeast-2.compute.amazonaws.com:3000", "https://169.254.234.110:4000",
                            "http://dentner.com", "https://dentner.com","http://10.0.10.41:443", "http://10.0.10.41:80",
                            "http://43.201.195.139:4000", "http://dentner.site", "https://dentner.site", "http://219.254.29.20:4000", "http://192.168.29.22:4000"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }
            }))
            .sessionManagement((sessionManagement) ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests((authorizeRequests) ->
                    authorizeRequests.requestMatchers(AUTH_WHITELIST).permitAll()
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated() //나머지 요청에 대해서는 인증을 요구함
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
	
	@Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
	
}
