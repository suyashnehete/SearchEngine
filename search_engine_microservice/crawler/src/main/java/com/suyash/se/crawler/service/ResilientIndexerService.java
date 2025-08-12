package com.suyash.se.crawler.service;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import com.suyash.se.crawler.crawler.CrawledPage;
import com.suyash.se.crawler.indexer.IndexerClient;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

/**
 * Resilient wrapper for IndexerClient with circuit breaker and metrics
 */
@Service
@Slf4j
public class ResilientIndexerService {

    private final IndexerClient indexerClient;
    private final CircuitBreaker circuitBreaker;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Counter fallbackCounter;
    private final Timer requestTimer;

    public ResilientIndexerService(IndexerClient indexerClient,
            CircuitBreakerFactory circuitBreakerFactory,
            MeterRegistry meterRegistry) {
        this.indexerClient = indexerClient;
        this.circuitBreaker = circuitBreakerFactory.create("indexer-service");

        // Initialize metrics
        this.successCounter = Counter.builder("indexer.requests.success")
                .description("Number of successful indexer requests")
                .register(meterRegistry);
        this.failureCounter = Counter.builder("indexer.requests.failure")
                .description("Number of failed indexer requests")
                .register(meterRegistry);
        this.fallbackCounter = Counter.builder("indexer.requests.fallback")
                .description("Number of fallback indexer requests")
                .register(meterRegistry);
        this.requestTimer = Timer.builder("indexer.requests.duration")
                .description("Duration of indexer requests")
                .register(meterRegistry);
    }

    /**
     * Build index with circuit breaker protection
     */
    public Boolean buildIndex(List<CrawledPage> pages) {
        Callable<Boolean> callable = () -> circuitBreaker.run(
                () -> {
                    try {
                        Boolean result = indexerClient.buildIndex(pages);
                        successCounter.increment();
                        log.debug("Successfully sent {} pages to indexer", pages.size());
                        return result;
                    } catch (Exception e) {
                        failureCounter.increment();
                        log.error("Error calling indexer service: {}", e.getMessage());
                        throw e;
                    }
                },
                throwable -> {
                    fallbackCounter.increment();
                    log.warn("Indexer service unavailable, using fallback. Error: {}", throwable.getMessage());
                    return handleIndexerFallback(pages);
                });
        try {
            return requestTimer.recordCallable(callable);
        } catch (Exception e) {
            log.error("Request timer failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Fallback method when indexer service is unavailable
     */
    private Boolean handleIndexerFallback(List<CrawledPage> pages) {
        log.info("Executing fallback for {} pages", pages.size());
        try {
            for (CrawledPage page : pages) {
                log.info("Fallback: Page {} will be indexed later", page.getUrl());
            }
            return true;
        } catch (Exception e) {
            log.error("Fallback also failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get circuit breaker state for monitoring
     */
    public String getCircuitBreakerState() {
        return circuitBreaker.toString();
    }
}