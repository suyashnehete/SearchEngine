package com.suyash.search_engine_api.cache;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final DoublyLinkedList<K, V> list;
    private final long ttl; // Time to live in minutes
    private final ExpirationPolicy expirationPolicy;
    private final ScheduledExecutorService scheduler;
    private final ReentrantLock lock = new ReentrantLock();

    public LRUCache(int capacity, long ttlInMinutes, ExpirationPolicy policy) {
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>();
        this.list = new DoublyLinkedList<>();
        this.ttl = ttlInMinutes * 60 * 1000; // Convert minutes to milliseconds
        this.expirationPolicy = policy;

        // Schedule a background cleanup task to run every 5 minutes
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.scheduler.scheduleAtFixedRate(this::cleanupExpiredEntries, 5, 5, TimeUnit.MINUTES);
    }

    public V get(K key) {
        lock.lock();
        try {
            if (!map.containsKey(key)) {
                return null;
            }
            Node<K, V> node = map.get(key);

            // Update timestamp for AFTER_ACCESS policy
            if (expirationPolicy == ExpirationPolicy.AFTER_ACCESS) {
                node.timestamp = System.currentTimeMillis();
            }

            // Check if the entry has expired
            if (isExpired(node)) {
                remove(key);
                return null;
            }

            list.moveToFront(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            if (map.containsKey(key)) {
                Node<K, V> node = map.get(key);
                node.value = value;
                node.timestamp = System.currentTimeMillis(); // Update timestamp
                list.moveToFront(node);
            } else {
                if (map.size() == capacity) {
                    Node<K, V> removedNode = list.removeLast();
                    map.remove(removedNode.key);
                }
                Node<K, V> newNode = new Node<>(key, value, System.currentTimeMillis());
                map.put(key, newNode);
                list.addFirst(newNode);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean isExpired(Node<K, V> node) {
        return System.currentTimeMillis() - node.timestamp > ttl;
    }

    private void remove(K key) {
        lock.lock();
        try {
            Node<K, V> node = map.remove(key);
            if (node != null) {
                list.remove(node);
            }
        } finally {
            lock.unlock();
        }
    }

    private void cleanupExpiredEntries() {
        lock.lock();
        try {
            Iterator<Map.Entry<K, Node<K, V>>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, Node<K, V>> entry = iterator.next();
                if (isExpired(entry.getValue())) {
                    iterator.remove();
                    list.remove(entry.getValue());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public Map<K, V> getAllCache() {
        lock.lock();
        try {
            Map<K, V> cache = new LinkedHashMap<>();

            for (Map.Entry<K, Node<K, V>> mapEntry : map.entrySet()) {
                K key = mapEntry.getKey();
                V value = get(key);

                cache.put(key, value);
            }
            return cache;
        } finally {
            lock.unlock();
        }
    }

    public enum ExpirationPolicy {
        AFTER_WRITE,
        AFTER_ACCESS
    }

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;
        long timestamp; // Timestamp when the node was created or updated

        Node(K key, V value, long timestamp) {
            this.key = key;
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    private static class DoublyLinkedList<K, V> {
        private Node<K, V> head;
        private Node<K, V> tail;

        public DoublyLinkedList() {
            head = new Node<>(null, null, 0);
            tail = new Node<>(null, null, 0);
            head.next = tail;
            tail.prev = head;
        }

        public void addFirst(Node<K, V> node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }

        public void moveToFront(Node<K, V> node) {
            remove(node);
            addFirst(node);
        }

        public Node<K, V> removeLast() {
            if (tail.prev == head) {
                return null;
            }
            return remove(tail.prev);
        }

        public Node<K, V> remove(Node<K, V> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
            return node;
        }
    }
}
