package com.suyash.se.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoints
                        .pathMatchers("/auth-server/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/query-service/**").permitAll() // Allow public search
                        .pathMatchers(HttpMethod.POST, "/crawler-service/crawler").permitAll() // Allow public URL
                                                                                               // submission

                        // Admin only endpoints - System Management
                        .pathMatchers("/crawler-service/admin/**").hasRole("ADMIN")
                        .pathMatchers("/indexer-service/admin/**").hasRole("ADMIN")
                        .pathMatchers("/monitoring/**").hasRole("ADMIN")
                        .pathMatchers("/analytics/**").hasRole("ADMIN")

                        // Admin only - Dangerous operations
                        .pathMatchers(HttpMethod.DELETE, "/indexer-service/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/indexer-service/reindex").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/indexer-service/optimize").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/crawler-service/start").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/crawler-service/stop").hasRole("ADMIN")

                        // Authenticated endpoints
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwkSetUri("http://localhost:8080/.well-known/jwks.json")))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}