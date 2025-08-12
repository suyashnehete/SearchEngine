package com.suyash.se.crawler.metrics;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for tracking crawler-specific metrics
 */
@Service
@Slf4j
public class CrawlerMetricsService {

    private final Counter urlsProcessedCounter;
    private final Counter urlsFailedCounter;
    private final Counter pagesIndexedCounter;
    private final Timer crawlingTimer;
    private final Timer indexingTimer;
    private final AtomicLong activeThreads = new AtomicLong(0);
    private final AtomicLong queueSize = new AtomicLong(0);

    public CrawlerMetricsService(MeterRegistry meterRegistry) {
        // Initialize counters
        this.urlsProcessedCounter = Counter.builder("crawler.urls.processed")
                .description("Total number of URLs processed")
                .register(meterRegistry);

        this.urlsFailedCounter = Counter.builder("crawler.urls.failed")
                .description("Total number of URLs that failed to crawl")
                .register(meterRegistry);

        this.pagesIndexedCounter = Counter.builder("crawler.pages.indexed")
                .description("Total number of pages sent for indexing")
                .register(meterRegistry);

        // Initialize timers
        this.crawlingTimer = Timer.builder("crawler.crawling.duration")
                .description("Time taken to crawl a single page")
                .register(meterRegistry);

        this.indexingTimer = Timer.builder("crawler.indexing.duration")
                .description("Time taken to send pages for indexing")
                .register(meterRegistry);

        // Initialize gauges
        Gauge.builder("crawler.threads.active", activeThreads, AtomicLong::get)
                .description("Number of active crawler threads")
                .register(meterRegistry);

        Gauge.builder("crawler.queue.size", queueSize, AtomicLong::get)
                .description("Current size of the crawling queue")
                .register(meterRegistry);
    }

    /**
     * Record a successfully processed URL
     */
    public void recordUrlProcessed() {
        urlsProcessedCounter.increment();
        log.debug("URL processed counter incremented");
    }

    /**
     * Record a failed URL
     */
    public void recordUrlFailed() {
        urlsFailedCounter.increment();
        log.debug("URL failed counter incremented");
    }

    /**
     * Record pages sent for indexing
     */
    public void recordPagesIndexed(int count) {
        pagesIndexedCounter.increment(count);
        log.debug("Pages indexed counter incremented by {}", count);
    }

    /**
     * Record crawling time
     */
    public Timer.Sample startCrawlingTimer() {
        return Timer.start();
    }

    /**
     * Stop crawling timer
     */
    public void stopCrawlingTimer(Timer.Sample sample) {
        sample.stop(crawlingTimer);
    }

    /**
     * Record indexing time
     */
    public Timer.Sample startIndexingTimer() {
        return Timer.start();
    }

    /**
     * Stop indexing timer
     */
    public void stopIndexingTimer(Timer.Sample sample) {
        sample.stop(indexingTimer);
    }

    /**
     * Update active threads count
     */
    public void setActiveThreads(long count) {
        activeThreads.set(count);
    }

    /**
     * Update queue size
     */
    public void setQueueSize(long size) {
        queueSize.set(size);
    }

    /**
     * Get active threads count
     */
    public double getActiveThreads() {
        return activeThreads.get();
    }

    /**
     * Get queue size
     */
    public double getQueueSize() {
        return queueSize.get();
    }

    /**
     * Get metrics summary
     */
    public MetricsSummary getMetricsSummary() {
        return MetricsSummary.builder()
                .urlsProcessed((long) urlsProcessedCounter.count())
                .urlsFailed((long) urlsFailedCounter.count())
                .pagesIndexed((long) pagesIndexedCounter.count())
                .activeThreads(activeThreads.get())
                .queueSize(queueSize.get())
                .averageCrawlingTime(crawlingTimer.mean(null))
                .averageIndexingTime(indexingTimer.mean(null))
                .build();
    }

    /**
     * Metrics summary data class
     */
    public static class MetricsSummary {
        public final long urlsProcessed;
        public final long urlsFailed;
        public final long pagesIndexed;
        public final long activeThreads;
        public final long queueSize;
        public final double averageCrawlingTime;
        public final double averageIndexingTime;

        private MetricsSummary(Builder builder) {
            this.urlsProcessed = builder.urlsProcessed;
            this.urlsFailed = builder.urlsFailed;
            this.pagesIndexed = builder.pagesIndexed;
            this.activeThreads = builder.activeThreads;
            this.queueSize = builder.queueSize;
            this.averageCrawlingTime = builder.averageCrawlingTime;
            this.averageIndexingTime = builder.averageIndexingTime;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private long urlsProcessed;
            private long urlsFailed;
            private long pagesIndexed;
            private long activeThreads;
            private long queueSize;
            private double averageCrawlingTime;
            private double averageIndexingTime;

            public Builder urlsProcessed(long urlsProcessed) {
                this.urlsProcessed = urlsProcessed;
                return this;
            }

            public Builder urlsFailed(long urlsFailed) {
                this.urlsFailed = urlsFailed;
                return this;
            }

            public Builder pagesIndexed(long pagesIndexed) {
                this.pagesIndexed = pagesIndexed;
                return this;
            }

            public Builder activeThreads(long activeThreads) {
                this.activeThreads = activeThreads;
                return this;
            }

            public Builder queueSize(long queueSize) {
                this.queueSize = queueSize;
                return this;
}

public Builder averageCrawlingTime(double averageCrawlingTime) {
                this.averageCrawlingTime = averageCrawlingTime;
                return this;
            }

            public Builder averageIndexingTime(double averageIndexingTime) {
                this.averageIndexingTime = averageIndexingTime;
                return this;
            }

            public MetricsSummary build() {
                return new MetricsSummary(this);
            }
        }
    }
}