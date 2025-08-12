package com.suyash.se.crawler.service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;

/**
 * Rate limiting service to prevent overwhelming target websites
 */
@Service
@Slf4j
public class RateLimitingService {

    @Value("${crawler.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${crawler.rate-limit.requests-per-domain:10}")
    private int requestsPerDomainPerMinute;

    private final ConcurrentHashMap<String, Bucket> domainBuckets = new ConcurrentHashMap<>();
    private final Bucket globalBucket;
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong blockedRequests = new AtomicLong(0);

    public RateLimitingService(@Value("${crawler.rate-limit.requests-per-minute:60}") int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
        
        // Global rate limiter
        Bandwidth globalLimit = Bandwidth.classic(requestsPerMinute, Refill.intervally(requestsPerMinute, Duration.ofMinutes(1)));
        this.globalBucket = Bucket.builder()
            .addLimit(globalLimit)
            .build();
    }

    /**
     * Check if request is allowed for the given URL
     */
    public boolean isAllowed(String url) {
        totalRequests.incrementAndGet();
        
        try {
            String domain = extractDomain(url);
            
            // Check global rate limit first
            if (!globalBucket.tryConsume(1)) {
                blockedRequests.incrementAndGet();
                log.warn("Global rate limit exceeded. Total requests: {}, Blocked: {}", 
                    totalRequests.get(), blockedRequests.get());
                return false;
            }
            
            // Check domain-specific rate limit
            Bucket domainBucket = getDomainBucket(domain);
            if (!domainBucket.tryConsume(1)) {
                blockedRequests.incrementAndGet();
                log.warn("Domain rate limit exceeded for: {}. Total requests: {}, Blocked: {}", 
                    domain, totalRequests.get(), blockedRequests.get());
                return false;
            }
            
            log.debug("Request allowed for URL: {} (Domain: {})", url, domain);
            return true;
            
        } catch (Exception e) {
            log.error("Error checking rate limit for URL: {}, Error: {}", url, e.getMessage());
            // Allow request if rate limiting fails
            return true;
        }
    }

    /**
     * Get or create bucket for domain
     */
    private Bucket getDomainBucket(String domain) {
        return domainBuckets.computeIfAbsent(domain, d -> {
            Bandwidth domainLimit = Bandwidth.classic(requestsPerDomainPerMinute, 
                Refill.intervally(requestsPerDomainPerMinute, Duration.ofMinutes(1)));
            return Bucket.builder()
                .addLimit(domainLimit)
                .build();
        });
    }

    /**
     * Extract domain from URL
     */
    private String extractDomain(String url) {
        try {
            java.net.URL urlObj = new java.net.URL(url);
            return urlObj.getHost().toLowerCase();
        } catch (Exception e) {
            log.warn("Could not extract domain from URL: {}", url);
            return "unknown";
        }
    }

    /**
     * Get rate limiting statistics
     */
    public RateLimitStats getStats() {
        return RateLimitStats.builder()
            .totalRequests(totalRequests.get())
            .blockedRequests(blockedRequests.get())
            .allowedRequests(totalRequests.get() - blockedRequests.get())
            .blockRate(totalRequests.get() > 0 ? (double) blockedRequests.get() / totalRequests.get() : 0.0)
            .activeDomains(domainBuckets.size())
            .globalRateLimit(requestsPerMinute)
            .domainRateLimit(requestsPerDomainPerMinute)
            .build();
    }

    /**
     * Reset statistics (for testing)
     */
    public void resetStats() {
        totalRequests.set(0);
        blockedRequests.set(0);
        domainBuckets.clear();
    }

    /**
     * Rate limiting statistics
     */
    public static class RateLimitStats {
        public final long totalRequests;
        public final long blockedRequests;
        public final long allowedRequests;
        public final double blockRate;
        public final int activeDomains;
        public final int globalRateLimit;
        public final int domainRateLimit;

        private RateLimitStats(Builder builder) {
            this.totalRequests = builder.totalRequests;
            this.blockedRequests = builder.blockedRequests;
            this.allowedRequests = builder.allowedRequests;
            this.blockRate = builder.blockRate;
            this.activeDomains = builder.activeDomains;
            this.globalRateLimit = builder.globalRateLimit;
            this.domainRateLimit = builder.domainRateLimit;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private long totalRequests;
            private long blockedRequests;
            private long allowedRequests;
            private double blockRate;
            private int activeDomains;
            private int globalRateLimit;
            private int domainRateLimit;

            public Builder totalRequests(long totalRequests) {
                this.totalRequests = totalRequests;
                return this;
            }

            public Builder blockedRequests(long blockedRequests) {
                this.blockedRequests = blockedRequests;
                return this;
            }

            public Builder allowedRequests(long allowedRequests) {
                this.allowedRequests = allowedRequests;
                return this;
            }

            public Builder blockRate(double blockRate) {
                this.blockRate = blockRate;
                return this;
            }

            public Builder activeDomains(int activeDomains) {
                this.activeDomains = activeDomains;
                return this;
            }

            public Builder globalRateLimit(int globalRateLimit) {
                this.globalRateLimit = globalRateLimit;
                return this;
            }

            public Builder domainRateLimit(int domainRateLimit) {
                this.domainRateLimit = domainRateLimit;
                return this;
            }

            public RateLimitStats build() {
                return new RateLimitStats(this);
            }
        }
    }
}