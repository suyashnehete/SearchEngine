package com.suyash.se.crawler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;import lombok.Data;

/**
 * Request DTO for URL submission with validation
 */
@Data
public class UrlRequest {
    
    @NotBlank(message = "URL cannot be blank")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    @Pattern(
        regexp = "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$",
        message = "URL must be a valid HTTP or HTTPS URL"
    )
    private String url;
    
    /**
     * Optional priority for crawling (1-10, default 5)
     */
    @Pattern(regexp = "^([1-9]|10)$", message = "Priority must be between 1 and 10")
    private String priority = "5";
    
    /**
     * Optional depth limit for crawling
     */
    @Pattern(regexp = "^[1-9]\\d*$", message = "Depth must be a positive integer")
    private String maxDepth = "3";
}