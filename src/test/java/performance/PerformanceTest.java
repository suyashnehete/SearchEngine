package performance;

import search.CacheSearchEngine;
import core.SearchResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceTest {
    private CacheSearchEngine searchEngine;
    private final Random random = new Random();
    private static final int WORD_COUNT = 100_000;
    private static final int QUERY_COUNT = 1000;

    @BeforeEach
    void setUp() {
        searchEngine = new CacheSearchEngine();
        // Generate and insert random words
        Set<String> words = generateRandomWords();
        for (String word : words) {
            searchEngine.insert(word);
        }
    }

    @AfterEach
    void tearDown() {
        searchEngine.shutdown();
    }

    @Test
    void testSearchPerformance() {
        // Warm up
        for (int i = 0; i < 100; i++) {
            searchEngine.fuzzySearch("test", 2);
        }

        // Measure performance
        long startTime = System.nanoTime();
        for (int i = 0; i < QUERY_COUNT; i++) {
            List<SearchResult> results = searchEngine.fuzzySearch("test", 2);
            assertNotNull(results);
        }
        long endTime = System.nanoTime();

        double averageQueryTime = (endTime - startTime) / (double) QUERY_COUNT;
        double millisPerQuery = TimeUnit.NANOSECONDS.toMillis((long) averageQueryTime);

        System.out.printf("Average query time: %.2f ms%n", millisPerQuery);
        assertTrue(millisPerQuery < 100, "Query time should be less than 100ms");
    }

    @Test
    void testCacheEffectiveness() {
        String query = "test";

        // First query - should be slow
        long startTime1 = System.nanoTime();
        searchEngine.fuzzySearch(query, 2);
        long endTime1 = System.nanoTime();

        // Second query - should be faster due to cache
        long startTime2 = System.nanoTime();
        searchEngine.fuzzySearch(query, 2);
        long endTime2 = System.nanoTime();

        double firstQueryTime = TimeUnit.NANOSECONDS.toMillis(endTime1 - startTime1);
        double secondQueryTime = TimeUnit.NANOSECONDS.toMillis(endTime2 - startTime2);

        System.out.printf("First query time: %.2f ms%n", firstQueryTime);
        System.out.printf("Second query time: %.2f ms%n", secondQueryTime);

        assertTrue(secondQueryTime < firstQueryTime,
                "Cached query should be faster");
    }

    private Set<String> generateRandomWords() {
        Set<String> words = new HashSet<>();
        String chars = "abcdefghijklmnopqrstuvwxyz";

        while (words.size() < WORD_COUNT) {
            StringBuilder word = new StringBuilder();
            int length = 3 + random.nextInt(8); // words of length 3-10

            for (int i = 0; i < length; i++) {
                word.append(chars.charAt(random.nextInt(chars.length())));
            }

            words.add(word.toString());
        }

        return words;
    }
}
