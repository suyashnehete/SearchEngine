package com.suyash.se.crawler.messaging;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.suyash.se.crawler.crawler.CrawledPage;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for publishing crawled pages to Kafka topics
 * Handles batching, error handling, and monitoring
 */
@Service
@Slf4j
public class CrawledPagePublisher {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Topic names
    private static final String CRAWLED_PAGES_TOPIC = "crawled-pages";
    private static final String CRAWLED_PAGES_BATCH_TOPIC = "crawled-pages-batch";
    private static final String CRAWLING_EVENTS_TOPIC = "crawling-events";

    /**
     * Publish a single crawled page
     */
    public void publishCrawledPage(CrawledPage page) {
        try {
            String key = generateKey(page);
            
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                CRAWLED_PAGES_TOPIC, 
                key, 
                page
            );
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Published crawled page: {} to partition: {} with offset: {}", 
                        page.getUrl(), 
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish crawled page: {}", page.getUrl(), ex);
                }
            });
            
        } catch (Exception e) {
            log.error("Error publishing crawled page: {}", page.getUrl(), e);
        }
    }

    /**
     * Publish a batch of crawled pages
     */
    public void publishCrawledPagesBatch(List<CrawledPage> pages) {
        if (pages == null || pages.isEmpty()) {
            log.warn("Attempted to publish empty or null page batch");
            return;
        }

        try {
            // Create batch message
            CrawledPageBatch batch = CrawledPageBatch.builder()
                .pages(pages)
                .batchId(java.util.UUID.randomUUID().toString())
                .timestamp(System.currentTimeMillis())
                .size(pages.size())
                .build();

            String key = "batch-" + batch.getBatchId();
            
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                CRAWLED_PAGES_BATCH_TOPIC, 
                key, 
                batch
            );
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Published crawled pages batch: {} pages to partition: {} with offset: {}", 
                        pages.size(), 
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish crawled pages batch: {} pages", pages.size(), ex);
                    // Fallback: try to publish individual pages
                    publishIndividualPages(pages);
                }
            });
            
        } catch (Exception e) {
            log.error("Error publishing crawled pages batch", e);
            // Fallback: try to publish individual pages
            publishIndividualPages(pages);
        }
    }

    /**
     * Publish crawling events (start, complete, error)
     */
    public void publishCrawlingEvent(CrawlingEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                CRAWLING_EVENTS_TOPIC, 
                event.getEventType().toString(), 
                event
            );
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Published crawling event: {}", event.getEventType());
                } else {
                    log.error("Failed to publish crawling event: {}", event.getEventType(), ex);
                }
            });
            
        } catch (Exception e) {
            log.error("Error publishing crawling event: {}", event.getEventType(), e);
        }
    }

    /**
     * Fallback method to publish pages individually
     */
    private void publishIndividualPages(List<CrawledPage> pages) {
        log.info("Falling back to individual page publishing for {} pages", pages.size());
        for (CrawledPage page : pages) {
            publishCrawledPage(page);
        }
    }

    /**
     * Generate partition key based on URL domain for better distribution
     */
    private String generateKey(CrawledPage page) {
        try {
            java.net.URL url = new java.net.URL(page.getUrl());
            return url.getHost(); // Use domain as key for partitioning
        } catch (Exception e) {
            return "unknown-domain";
        }
    }

    /**
     * Get Kafka template metrics for monitoring
     */
    public void logMetrics() {
        try {
            log.info("Kafka Producer Metrics: {}", kafkaTemplate.metrics());
        } catch (Exception e) {
            log.error("Error retrieving Kafka metrics", e);
        }
    }
}