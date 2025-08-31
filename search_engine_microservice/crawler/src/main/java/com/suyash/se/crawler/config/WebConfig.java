package com.suyash.se.crawler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Explicitly disable all static resource handlers to prevent conflicts with controller mappings
        // This ensures that /admin/* paths are handled by controllers, not static resource handlers
    }
}