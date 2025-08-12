package com.suyash.se.crawler.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event object for crawling lifecycle events
 * Used for monitoring and coordination between services
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlingEvent {
    
    private String eventId;
    private EventType eventType;
    private String crawlerInstanceId;
    private long timestamp;
    private String url;
    private String message;
    private Object metadata;
    
    public enum EventType {
        CRAWLING_STARTED,
        CRAWLING_COMPLETED,
        CRAWLING_FAILED,
        URL_DISCOVERED,
        URL_PROCESSED,
        BATCH_PROCESSED,
        CRAWLER_SHUTDOWN
    }
    
    /**
     * Create a crawling started event
     */
    public static CrawlingEvent crawlingStarted(String crawlerInstanceId, String url) {
        return CrawlingEvent.builder()
            .eventId(java.util.UUID.randomUUID().toString())
            .eventType(EventType.CRAWLING_STARTED)
            .crawlerInstanceId(crawlerInstanceId)
            .timestamp(System.currentTimeMillis())
            .url(url)
            .message("Crawling started for URL: " + url)
            .build();
    }
    
    /**
     * Create a crawling completed event
     */
    public static CrawlingEvent crawlingCompleted(String crawlerInstanceId, String url, int pagesProcessed) {
        return CrawlingEvent.builder()
            .eventId(java.util.UUID.randomUUID().toString())
            .eventType(EventType.CRAWLING_COMPLETED)
            .crawlerInstanceId(crawlerInstanceId)
            .timestamp(System.currentTimeMillis())
            .url(url)
            .message("Crawling completed for URL: " + url + ", pages processed: " + pagesProcessed)
            .metadata(java.util.Map.of("pagesProcessed", pagesProcessed))
            .build();
    }
    
    /**
     * Create a crawling failed event
     */
    public static CrawlingEvent crawlingFailed(String crawlerInstanceId, String url, String error) {
        return CrawlingEvent.builder()
            .eventId(java.util.UUID.randomUUID().toString())
            .eventType(EventType.CRAWLING_FAILED)
            .crawlerInstanceId(crawlerInstanceId)
            .timestamp(System.currentTimeMillis())
            .url(url)
            .message("Crawling failed for URL: " + url + ", error: " + error)
            .metadata(java.util.Map.of("error", error))
            .build();
    }
}