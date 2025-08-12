package com.suyash.se.query.cache;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RedisCacheService<K, V> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Duration defaultTtl = Duration.ofMinutes(10); // Default TTL of 10 minutes

    /**
     * Get value from cache if present
     */
    public V getIfPresent(K key) {
        try {
            String redisKey = generateKey(key);
            Object value = redisTemplate.opsForValue().get(redisKey);
            if (value != null) {
                return (V) value;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error getting value from Redis cache: " + e.getMessage());
            return null;
        }
    }

    /**
     * Put value in cache with default TTL
     */
    public void put(K key, V value) {
        put(key, value, defaultTtl);
    }

/**
     * Put value in cache with custom TTL
     */
    public void put(K key, V value, Duration ttl) {
        try {
            String redisKey = generateKey(key);
         redisTemplate.opsForValue().set(redisKey, value, ttl);
        } catch (Exception e) {
            System.err.println("Error putting value in Redis cache: " + e.getMessage());
        }
    }

    /**
     * Remove value from cache
     */
    public void remove(K key) {
        try {
            String redisKey = generateKey(key);
            redisTemplate.delete(redisKey);
        } catch (Exception e) {
            System.err.println("Error removing value from Redis cache: " + e.getMessage());
        }
    }

    /**
     * Check if key exists in cache
     */
    public boolean containsKey(K key) {
        try {
            String redisKey = generateKey(key);
            return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
        } catch (Exception e) {
            System.err.println("Error checking key existence in Redis cache: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all cache entries as a map (for compatibility with LRU cache)
     * Note: This is expensive for large caches, use sparingly
     */
    public Map<K, V> asMap() {
        try {
            Map<K, V> result = new HashMap<>();
            String pattern = "query_cache:*";
            Set<String> keys = redisTemplate.keys(pattern);
            
            if (keys != null) {
                for (String redisKey : keys) {
                    try {
                        K originalKey = extractOriginalKey(redisKey);
                        V value = (V) redisTemplate.opsForValue().get(redisKey);
                        if (value != null) {
                            result.put(originalKey, value);
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing key " + redisKey + ": " + e.getMessage());
                    }
                }
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error getting all cache entries: " + e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Clear all cache entries
     */
    public void clear() {
        try {
            String pattern = "query_cache:*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            System.err.println("Error clearing Redis cache: " + e.getMessage());
        }
    }

    /**
     * Get cache size (approximate)
     */
    public long size() {
        try {
            String pattern = "query_cache:*";
            Set<String> keys = redisTemplate.keys(pattern);
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            System.err.println("Error getting cache size: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Set TTL for existing key
     */
    public void expire(K key, Duration ttl) {
        try {
            String redisKey = generateKey(key);
            redisTemplate.expire(redisKey, ttl);
        } catch (Exception e) {
            System.err.println("Error setting TTL for key: " + e.getMessage());
        }
    }

    /**
     * Get TTL for key
     */
    public long getTtl(K key) {
        try {
            String redisKey = generateKey(key);
            return redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("Error getting TTL for key: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Generate Redis key with namespace
     */
    private String generateKey(K key) {
        return "query_cache:" + key.toString();
    }

    /**
     * Extract original key from Redis key
     */
    @SuppressWarnings("unchecked")
    private K extractOriginalKey(String redisKey) {
        String keyStr = redisKey.replace("query_cache:", "");
        return (K) keyStr;
    }
}