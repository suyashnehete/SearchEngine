package search;

import cache.LRUCache;
import algo.LevenshteinDistance;
import algo.NGramIndexer;
import core.SearchResult;
import core.TrieNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CacheSearchEngine extends SearchEngine {

    private static final int CACHE_CAPACITY = 1000;
    private static final long CACHE_EXPIRE_MILLIS = 30 * 60 * 1000; // 30 minutes
    private static final int NGRAM_SIZE = 3;

    private final LRUCache<String, List<SearchResult>> searchCache;
    private final NGramIndexer nGramIndexer;
    private final ExecutorService executorService;

    public CacheSearchEngine() {
        super();

        this.searchCache = new LRUCache<>(CACHE_CAPACITY, CACHE_EXPIRE_MILLIS);
        this.nGramIndexer = new NGramIndexer(NGRAM_SIZE);
        this.executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );
    }


    /**
     * Inserts a word into the Trie and indexes it using n-grams.
     *
     * @param word the word to be inserted
     */
    @Override
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        lock.writeLock().lock();
        try {
            String normalizedWord = word.toLowerCase();
            super.insert(normalizedWord);
            nGramIndexer.indexWord(normalizedWord);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Performs a fuzzy search for a query word within a given Levenshtein distance,
     * using n-gram indexing and parallel processing.
     *
     * @param query       the query word
     * @param maxDistance the maximum Levenshtein distance
     * @return a list of search results matching the query within the given distance
     */
    @Override
    public List<SearchResult> fuzzySearch(String query, int maxDistance) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedQuery = query.toLowerCase();

        // Check cache first
        List<SearchResult> cachedResults = searchCache.get(normalizedQuery);
        if (cachedResults != null) {
            return cachedResults;
        }

        lock.readLock().lock();
        try {
            // Get candidates using n-gram index
            Set<String> candidates = nGramIndexer.getCandidates(normalizedQuery);

            // Process candidates in parallel
            List<Future<SearchResult>> futures = new ArrayList<>();
            for (String candidate : candidates) {
                futures.add(executorService.submit(() -> {
                    int distance = LevenshteinDistance.calculate(normalizedQuery, candidate);
                    if (distance <= maxDistance) {
                        TrieNode node = searchNode(candidate);
                        if (node != null) {
                            return new SearchResult(candidate, distance, node.getFrequency());
                        }
                    }
                    return null;
                }));
            }

            // Collect and sort results
            List<SearchResult> results = new ArrayList<>();
            for (Future<SearchResult> future : futures) {
                try {
                    SearchResult result = future.get();
                    if (result != null) {
                        results.add(result);
                    }
                } catch (Exception e) {
                    logger.error("Error processing fuzzy search result", e);
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

            // Cache the results
            searchCache.put(normalizedQuery, results);

            return results;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Shuts down the executor service gracefully.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
