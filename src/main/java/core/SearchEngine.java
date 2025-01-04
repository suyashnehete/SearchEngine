package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SearchEngine {
    private static final Logger logger = LoggerFactory.getLogger(SearchEngine.class);
    private final TrieNode root;
    private final ReadWriteLock lock;

    public SearchEngine() {
        this.root = new TrieNode();
        this.lock = new ReentrantReadWriteLock();
    }

    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }

        lock.writeLock().lock();
        try {
            TrieNode current = root;
            String normalizedWord = word.toLowerCase();

            for (char ch : normalizedWord.toCharArray()) {
                current.getChildren().putIfAbsent(ch, new TrieNode());
                current = current.getChildren().get(ch);
                current.addToTopSearches(normalizedWord);
            }

            current.setEndOfWord(true);
            current.setWord(normalizedWord);
            current.incrementFrequency();
            logger.debug("Inserted word: {} into the trie", word);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean search(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        lock.readLock().lock();
        try {
            TrieNode node = searchNode(word.toLowerCase());
            return node != null && node.isEndOfWord();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<String> searchWithPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return Collections.emptyList();
        }

        lock.readLock().lock();
        try {
            TrieNode node = searchNode(prefix.toLowerCase());
            if (node == null) {
                return Collections.emptyList();
            }

            List<String> results = new ArrayList<>();
            dfs(node, results);
            return results;
        } finally {
            lock.readLock().unlock();
        }
    }

    private TrieNode searchNode(String str) {
        TrieNode current = root;
        for (char ch : str.toCharArray()) {
            current = current.getChildren().get(ch);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private void dfs(TrieNode node, List<String> results) {
        if (node.isEndOfWord()) {
            results.add(node.getWord());
        }

        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            dfs(entry.getValue(), results);
        }
    }
}