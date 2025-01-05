package cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread-safe LRU (Least Recently Used) cache implementation with expiration support.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
public class LRUCache<K, V> {
    private final LinkedHashMap<K, CacheEntry<V>> cache;
    private final int capacity;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final long expireAfterMillis;

    /**
     * Constructs an LRUCache with the specified capacity and expiration time.
     *
     * @param capacity the maximum number of entries the cache can hold
     * @param expireAfterMillis the time in milliseconds after which an entry expires
     */
    public LRUCache(int capacity, long expireAfterMillis) {
        this.capacity = capacity;
        this.expireAfterMillis = expireAfterMillis;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > LRUCache.this.capacity;
            }
        };
    }

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key, or null if the key is not present or the entry is expired
     */
    public V get(K key) {
        lock.readLock().lock();
        try {
            CacheEntry<V> entry = cache.get(key);
            if (entry == null) {
                return null;
            }

            if (isExpired(entry)) {
                cache.remove(key);
                return null;
            }

            return entry.value;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Associates the specified value with the specified key in this cache.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Checks if the specified cache entry is expired.
     *
     * @param entry the cache entry to check
     * @return true if the entry is expired, false otherwise
     */
    private boolean isExpired(CacheEntry<V> entry) {
        return System.currentTimeMillis() - entry.timestamp > expireAfterMillis;
    }

    /**
     * A cache entry that holds a value and a timestamp.
     *
     * @param <V> the type of the value
     */
    private record CacheEntry<V>(V value, long timestamp) {
    }
}