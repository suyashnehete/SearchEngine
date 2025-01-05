package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import search.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The SearchEngine class provides methods to insert words into a Trie,
 * search for exact matches, search for words with a given prefix, and
 * perform fuzzy searches using Levenshtein Distance.
 */
public class SearchEngine {
    private static final Logger logger = LoggerFactory.getLogger(SearchEngine.class);
    private final TrieNode root;
    private final ReadWriteLock lock;

    /**
     * Constructs a new SearchEngine with an empty Trie and a read-write lock.
     */
    public SearchEngine() {
        this.root = new TrieNode();
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Inserts a word into the Trie.
     *
     * @param word the word to insert
     */
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

            logger.debug("Inserted word: {} into the Trie", word);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Searches for an exact match of a word in the Trie.
     *
     * @param word the word to search for
     * @return true if the word is found, false otherwise
     */
    public boolean search(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        lock.readLock().lock();
        try {
            TrieNode node = searchNode(word.toLowerCase());
            return (node != null && node.isEndOfWord());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Searches for words in the Trie that start with the given prefix.
     *
     * @param prefix the prefix to search for
     * @return a list of words that start with the prefix
     */
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

    /**
     * Finds the TrieNode corresponding to the last character of str,
     * or returns null if it doesn't exist.
     *
     * @param str the string to search for
     * @return the TrieNode corresponding to the last character of str, or null if it doesn't exist
     */
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

    /**
     * Depth-first search to collect all words under a given TrieNode.
     *
     * @param node the starting TrieNode
     * @param results the list to collect words into
     */
    private void dfs(TrieNode node, List<String> results) {
        if (node.isEndOfWord()) {
            results.add(node.getWord());
        }
        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            dfs(entry.getValue(), results);
        }
    }

    /**
     * Performs a fuzzy search by collecting all words in the Trie and
     * computing the Levenshtein Distance to the query. It then filters out
     * words with distance larger than maxDistance, builds a list of results,
     * and sorts them by frequency (descending) and distance (ascending).
     *
     * @param query the query string to search for
     * @param maxDistance the maximum allowed Levenshtein Distance
     * @return a list of SearchResult objects that match the query within the maxDistance
     */
    public List<SearchResult> fuzzySearch(String query, int maxDistance) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }

        lock.readLock().lock();
        try {
            List<SearchResult> results = new ArrayList<>();
            String normalizedQuery = query.toLowerCase();

            // Collect all words from the Trie
            List<String> allWords = new ArrayList<>();
            collectAllWords(root, allWords);

            // Find matches within distance
            for (String word : allWords) {
                int distance = LevenshteinDistance.calculate(normalizedQuery, word);
                if (distance <= maxDistance) {
                    TrieNode node = searchNode(word);
                    if (node != null) {
                        results.add(new SearchResult(word, distance, node.getFrequency()));
                    }
                }
            }

            // Sort primarily by frequency (descending), then by distance (ascending)
            results.sort((a, b) -> {
                // Compare frequency first (higher frequency -> earlier in the list)
                int freqComp = Integer.compare(b.frequency(), a.frequency());
                if (freqComp != 0) {
                    return freqComp;
                }
                // If frequency is the same, compare distance (lower distance -> earlier in the list)
                return Integer.compare(a.distance(), b.distance());
            });

            return results;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Recursively collect all words from the given TrieNode downwards.
     *
     * @param node the starting TrieNode
     * @param words the list to collect words into
     */
    private void collectAllWords(TrieNode node, List<String> words) {
        if (node.isEndOfWord()) {
            words.add(node.getWord());
        }
        for (TrieNode child : node.getChildren().values()) {
            collectAllWords(child, words);
        }
    }
}