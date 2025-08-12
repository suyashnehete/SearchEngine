package com.suyash.se.indexer.crawler;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.suyash.se.indexer.index.IndexerService;
import com.suyash.se.indexer.messaging.CrawledPageBatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class CrawledPageConsumer {

    private final IndexerService indexerService;

    /**
     * Process individual crawled pages
     */
    @KafkaListener(topics = "crawled-pages", groupId = "indexer-service-group", containerFactory = "kafkaListenerContainerFactory")
    public void processCrawledPage(
            @Payload CrawledPage page,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Processing single crawled page: {} from partition: {} offset: {}",
                    page.getUrl(), partition, offset);

            // Process single page
            indexerService.buildIndex(List.of(page));

            // Manual acknowledgment
            acknowledgment.acknowledge();

            log.info("Successfully processed crawled page: {}", page.getUrl());

        } catch (Exception e) {
            log.error("Error processing crawled page: {}", page.getUrl(), e);
            // Don't acknowledge - message will be retried
            throw e;
        }
    }

    /**
     * Process batches of crawled pages (more efficient)
     */
    @KafkaListener(
        topics = "crawled-pages-batch",
        groupId = "indexer-service-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void processCrawledPagesBatch(
            @Payload CrawledPageBatch batch,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            log.info("Processing crawled pages batch: {} pages, batchId: {} from partition: {} offset: {}", 
                batch.getSize(), batch.getBatchId(), partition, offset);
            
            List<CrawledPage> pages = batch.getPages();
            if (pages == null || pages.isEmpty()) {
                log.warn("Received empty batch: {}", batch.getBatchId());
                acknowledgment.acknowledge();
                return;
            }
            
            // Log batch details
            log.info("Batch details - Size: {}, Domains: {}, Total content size: {} bytes", 
                batch.getSize(), batch.getDomains(), batch.getTotalContentSize());
            
            // Process batch
            indexerService.buildIndex(pages);
            
            // Manual acknowledgment
            acknowledgment.acknowledge();
            
            log.info("Successfully processed crawled pages batch: {} pages", pages.size());
            
        } catch (Exception e) {
            log.error("Error processing crawled pages batch: {}", batch.getBatchId(), e);
            // Don't acknowledge - message will be retried
            throw e;
        }
    }

    /**
     * Legacy support for old message format
     */
    @KafkaListener(
        topics = "crawled_pages",
        groupId = "indexer-service-group-legacy",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void processLegacyCrawledPages(
            @Payload List<CrawledPage> pages,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            log.info("Processing legacy crawled pages: {} pages from partition: {} offset: {}", 
                pages.size(), partition, offset);
            
            if (pages == null || pages.isEmpty()) {
                log.warn("Received empty legacy page list");
                acknowledgment.acknowledge();
                return;
            }
            
            for (CrawledPage page : pages) {
                log.debug("Processing legacy crawled page: {}", page.getUrl());
            }
            
            // Process pages
            indexerService.buildIndex(pages);
            
            // Manual acknowledgment
            acknowledgment.acknowledge();
            
            log.info("Successfully processed legacy crawled pages: {} pages", pages.size());
            
        } catch (Exception e) {
            log.error("Error processing legacy crawled pages", e);
            // Don't acknowledge - message will be retried
            throw e;
        }
    }
}
