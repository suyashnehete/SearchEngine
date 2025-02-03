package com.suyash.se.query.cache;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class LRUCacheService<K, V> {
    final int CAPACITY = 1000;
    private final LRUCache<K, V> cache = new LRUCache<>(CAPACITY, 10, LRUCache.ExpirationPolicy.AFTER_ACCESS);

    public V getIfPresent(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public Map<K, V> asMap() {
        return cache.getAllCache();
    }
}
