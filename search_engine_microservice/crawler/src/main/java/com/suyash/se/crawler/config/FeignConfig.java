package com.suyash.se.crawler.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
public class FeignConfig {

    @Value("${service.internal.token:search-engine-internal-token-2024}")
    private String internalServiceToken;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignRequestInterceptor();
    }

    public class FeignRequestInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            // For now, just add the internal service header for identification
            // Since we've simplified security to permitAll(), we don't need complex token logic
            template.header("X-Internal-Service", "crawler-service");
        }
    }
}