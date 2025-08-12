package com.suyash.se.crawler.messaging;

import java.util.List;

import com.suyash.se.crawler.crawler.CrawledPage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Batch wrapper for multiple crawled pages
 * Optimizes Kafka message throughput by batching multiple pages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawledPageBatch {
    
    private String batchId;
    private List<CrawledPage> pages;
    private long timestamp;
    private int size;
    private String crawlerInstanceId;
    
    /**
     * Get total content size of all pages in batch
     */
    public long getTotalContentSize() {
        return pages.stream()
            .mapToLong(page -> page.getContent() != null ? page.getContent().length() : 0)
            .sum();
    }
    
    /**
     * Get all unique domains in this batch
     */
    public java.util.Set<String> getDomains() {
        return pages.stream()
            .map(page -> {
                try {
                    return new java.net.URL(page.getUrl()).getHost();
                } catch (Exception e) {
                    return "unknown";
                }
            })
            .collect(java.util.stream.Collectors.toSet());
    }
}