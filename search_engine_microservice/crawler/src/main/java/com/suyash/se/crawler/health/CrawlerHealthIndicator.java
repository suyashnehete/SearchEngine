package com.suyash.se.crawler.health;

import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.suyash.se.crawler.cache.RedisUrlTrackingService;
import com.suyash.se.crawler.service.RateLimitingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom health indicator for crawler service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CrawlerHealthIndicator implements HealthIndicator {

    private final RedisUrlTrackingService urlTrackingService;
    private final RateLimitingService rateLimitingService;

    @Override
    public Health health() {
        try {
            // Check URL tracking service
            long queueSize = urlTrackingService.getQueueSize();
            long visitedCount = urlTrackingService.getVisitedCount();

            // Check rate limiting service
            RateLimitingService.RateLimitStats rateLimitStats = rateLimitingService.getStats();

            Health.Builder healthBuilder = Health.up();

            // Add crawler-specific details
            healthBuilder
                    .withDetail("queueSize", queueSize)
                    .withDetail("visitedCount", visitedCount)
                    .withDetail("rateLimitStats", Map.of(
                            "totalRequests", rateLimitStats.totalRequests,
                            "blockedRequests", rateLimitStats.blockedRequests,
                            "blockRate", String.format("%.2f%%", rateLimitStats.blockRate * 100),
                            "activeDomains", rateLimitStats.activeDomains));

            // Check if queue is too large (potential issue)
            if (queueSize > 10000) {
                healthBuilder.withDetail("warning", "Queue size is very large: " + queueSize);
            }

            // Check if block rate is too high
            if (rateLimitStats.blockRate > 0.5) {
                healthBuilder.withDetail("warning",
                        "High block rate: " + String.format("%.2f%%", rateLimitStats.blockRate * 100));
            }

            return healthBuilder.build();

        } catch (Exception e) {
            log.error("Error checking crawler health: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", System.currentTimeMillis())
                    .build();
        }
    }
}