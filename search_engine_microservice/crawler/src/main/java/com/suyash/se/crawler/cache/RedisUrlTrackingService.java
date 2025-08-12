package com.suyash.se.crawler.cache;

import java.time.Duration;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisUrlTrackingService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String VISITED_URLS_KEY = "crawler:visited_urls";
    private static final String URL_QUEUE_KEY = "crawler:url_queue";
    private static final Duration DEFAULT_TTL = Duration.ofHours(24); // URLs expire after 24 hours

    /**
     * Check if URL has been visited
     */
    public boolean isVisited(String url) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(VISITED_URLS_KEY, url));
        } catch (Exception e) {
            System.err.println("Error checking if URL is visited: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mark URL as visited
     */
    public void markAsVisited(String url) {
        try {
            redisTemplate.opsForSet().add(VISITED_URLS_KEY, url);
            // Set TTL for the entire set (this will reset TTL each time, which is fine for
            // our use case)
            redisTemplate.expire(VISITED_URLS_KEY, DEFAULT_TTL);
        } catch (Exception e) {
            System.err.println("Error marking URL as visited: " + e.getMessage());
        }
    }

    /**
     * Add URL to crawling queue
     */
    public void addToQueue(String url) {
        try {
            if (!isVisited(url) && !isInQueue(url)) {
                redisTemplate.opsForSet().add(URL_QUEUE_KEY, url);
                redisTemplate.expire(URL_QUEUE_KEY, DEFAULT_TTL);
            }
        } catch (Exception e) {
            System.err.println("Error adding URL to queue: " + e.getMessage());
        }
    }

    /**
     * Get next URL from queue
     */
    public String getNextUrl() {
        try {
            return (String) redisTemplate.opsForSet().pop(URL_QUEUE_KEY);
        } catch (Exception e) {
            System.err.println("Error getting next URL from queue: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if URL is in queue
     */
    public boolean isInQueue(String url) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(URL_QUEUE_KEY, url));
        } catch (Exception e) {
            System.err.println("Error checking if URL is in queue: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get queue size
     */
    public long getQueueSize() {
        try {
            return redisTemplate.opsForSet().size(URL_QUEUE_KEY);
        } catch (Exception e) {
            System.err.println("Error getting queue size: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get visited URLs count
     */
    public long getVisitedCount() {
        try {
            return redisTemplate.opsForSet().size(VISITED_URLS_KEY);
        } catch (Exception e) {
            System.err.println("Error getting visited count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Clear all visited URLs (for testing/reset purposes)
     */
    public void clearVisited() {
        try {
            redisTemplate.delete(VISITED_URLS_KEY);
        } catch (Exception e) {
            System.err.println("Error clearing visited URLs: " + e.getMessage());
        }
    }

    /**
     * Clear URL queue (for testing/reset purposes)
     */
    public void clearQueue() {
        try {
            redisTemplate.delete(URL_QUEUE_KEY);
        } catch (Exception e) {
            System.err.println("Error clearing URL queue: " + e.getMessage());
        }
    }

    /**
     * Get all visited URLs (for debugging - use sparingly)
     */
    public Set<Object> getAllVisitedUrls() {
        try {
            return redisTemplate.opsForSet().members(VISITED_URLS_KEY);
        } catch (Exception e) {
            System.err.println("Error getting all visited URLs: " + e.getMessage());
            return Set.of();
        }
    }

    /**
     * Check if queue is empty
     */
    public boolean isQueueEmpty() {
        return getQueueSize() == 0;
    }
}