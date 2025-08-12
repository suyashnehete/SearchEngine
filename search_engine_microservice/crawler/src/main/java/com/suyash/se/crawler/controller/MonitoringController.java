package com.suyash.se.crawler.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyash.se.crawler.cache.RedisUrlTrackingService;
import com.suyash.se.crawler.metrics.CrawlerMetricsService;
import com.suyash.se.crawler.service.RateLimitingService;
import com.suyash.se.crawler.service.ResilientIndexerService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for monitoring and metrics endpoints
 */
@RestController
@RequestMapping("monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final CrawlerMetricsService metricsService;
    private final RateLimitingService rateLimitingService;
    private final RedisUrlTrackingService urlTrackingService;
    private final ResilientIndexerService resilientIndexerService;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Crawler metrics
        CrawlerMetricsService.MetricsSummary crawlerMetrics = metricsService.getMetricsSummary();
        metrics.put("crawler", Map.of(
            "urlsProcessed", crawlerMetrics.urlsProcessed,
            "urlsFailed", crawlerMetrics.urlsFailed,
            "pagesIndexed", crawlerMetrics.pagesIndexed,
            "activeThreads", crawlerMetrics.activeThreads,
            "queueSize", crawlerMetrics.queueSize,
            "averageCrawlingTime", String.format("%.2f ms", crawlerMetrics.averageCrawlingTime),
            "averageIndexingTime", String.format("%.2f ms", crawlerMetrics.averageIndexingTime)
        ));
        
        // Rate limiting metrics
        RateLimitingService.RateLimitStats rateLimitStats = rateLimitingService.getStats();
        metrics.put("rateLimiting", Map.of(
            "totalRequests", rateLimitStats.totalRequests,
            "blockedRequests", rateLimitStats.blockedRequests,
            "allowedRequests", rateLimitStats.allowedRequests,
            "blockRate", String.format("%.2f%%", rateLimitStats.blockRate * 100),
            "activeDomains", rateLimitStats.activeDomains,
            "globalRateLimit", rateLimitStats.globalRateLimit,
            "domainRateLimit", rateLimitStats.domainRateLimit
        ));
        
        // URL tracking metrics
        metrics.put("urlTracking", Map.of(
            "queueSize", urlTrackingService.getQueueSize(),
            "visitedCount", urlTrackingService.getVisitedCount(),
            "queueEmpty", urlTrackingService.isQueueEmpty()
        ));
        
        // Circuit breaker status
        metrics.put("circuitBreaker", Map.of(
            "indexerServiceState", resilientIndexerService.getCircuitBreakerState()
        ));
        
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/health-summary")
    public ResponseEntity<Map<String, Object>> getHealthSummary() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Check various components
            long queueSize = urlTrackingService.getQueueSize();
            long visitedCount = urlTrackingService.getVisitedCount();
            RateLimitingService.RateLimitStats rateLimitStats = rateLimitingService.getStats();
            
            // Overall health status
            boolean isHealthy = true;
            StringBuilder issues = new StringBuilder();
            
            // Check for potential issues
            if (queueSize > 10000) {
                isHealthy = false;
                issues.append("Queue size too large (").append(queueSize).append("); ");
            }
            
            if (rateLimitStats.blockRate > 0.8) {
                isHealthy = false;
                issues.append("High block rate (").append(String.format("%.1f%%", rateLimitStats.blockRate * 100)).append("); ");
            }
            
            health.put("status", isHealthy ? "UP" : "DOWN");
            health.put("timestamp", System.currentTimeMillis());
            
            if (!isHealthy) {
                health.put("issues", issues.toString());
            }
            
            health.put("details", Map.of(
                "queueSize", queueSize,
                "visitedCount", visitedCount,
                "blockRate", String.format("%.2f%%", rateLimitStats.blockRate * 100),
                "activeDomains", rateLimitStats.activeDomains
            ));
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(health);
        }
    }

    @GetMapping("/reset-stats")
    public ResponseEntity<Map<String, String>> resetStats() {
        try {
            rateLimitingService.resetStats();
            return ResponseEntity.ok(Map.of(
                "message", "Statistics reset successfully",
                "timestamp", String.valueOf(System.currentTimeMillis())
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to reset statistics",
                "message", e.getMessage()
            ));
        }
    }
}