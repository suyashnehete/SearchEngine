package com.suyash.se.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret:mySecretKey12345678901234567890123456789012345678901234567890}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(reactiveJwtDecoder())
                                .jwtAuthenticationConverter(new JwtAuthenticationConverter())))
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoints - no authentication required
                        .pathMatchers("/api/auth-server/**").permitAll()
                        .pathMatchers("/api/auth-server/api/auth/**").permitAll() // Allow auth API calls through gateway
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/query-service/**").permitAll() // Allow public search
                        .pathMatchers(HttpMethod.POST, "/api/crawler-service/crawler").permitAll() // Allow public URL submission
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow CORS preflight

                        // Admin only endpoints - System Management
                        .pathMatchers("/api/crawler-service/admin/**").hasRole("ADMIN")
                        .pathMatchers("/api/indexer-service/admin/**").hasRole("ADMIN")
                        .pathMatchers("/monitoring/**").hasRole("ADMIN")
                        .pathMatchers("/analytics/**").hasRole("ADMIN")

                        // Admin only - Dangerous operations
                        .pathMatchers(HttpMethod.DELETE, "/api/indexer-service/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/indexer-service/reindex").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/indexer-service/optimize").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/crawler-service/start").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/crawler-service/stop").hasRole("ADMIN")

                        // All other endpoints require authentication
                        .anyExchange().authenticated())
                .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        String angularPort = System.getProperty("ANGULAR_PORT", "4200");
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:" + angularPort));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}