package com.suyash.se.query.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for search queries with validation
 */
@Data
public class SearchRequest {
    
    @NotBlank(message = "Query cannot be blank")
    @Size(min = 1, max = 500, message = "Query must be between 1 and 500 characters")
    private String query;
    
    @Min(value = 1, message = "Page must be at least 1")
    @Max(value = 1000, message = "Page cannot exceed 1000")
    private int page = 1;
    
    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    private int size = 10;
    
    @Min(value = 1, message = "TopK must be at least 1")
    @Max(value = 1000, message = "TopK cannot exceed 1000")
    private int topK = 50;
    
    /**
     * Optional filters for search
     */
    @Size(max = 10, message = "Cannot have more than 10 tags")
    private List<@Size(max = 50, message = "Tag cannot exceed 50 characters") String> tags;
    
    /**
     * Optional user ID for personalized search
     */
    @Size(max = 100, message = "User ID cannot exceed 100 characters")
    private String userId;
}