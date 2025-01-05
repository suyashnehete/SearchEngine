package search;

import core.SearchResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CacheSearchEngineTest {

    private CacheSearchEngine cacheSearchEngine;

    @BeforeEach
    void setUp() {
        // Initialize CacheSearchEngine and insert sample data
        cacheSearchEngine = new CacheSearchEngine();
        cacheSearchEngine.insert("hello");
        cacheSearchEngine.insert("helo");
        cacheSearchEngine.insert("help");
        cacheSearchEngine.insert("helicopter");
        cacheSearchEngine.insert("world");
    }

    @AfterEach
    void tearDown() {
        // Shutdown the CacheSearchEngine
        cacheSearchEngine.shutdown();
    }

    @Test
    void fuzzySearch() {
        // Perform fuzzy search with distance 1
        List<SearchResult> results = cacheSearchEngine.fuzzySearch("helo", 1);

        // Verify the results
        assertEquals(3, results.size());
    }

    @Test
    void fuzzySearchWithFrequency() {
        // Insert "hello" multiple times to boost its frequency
        cacheSearchEngine.insert("hello");
        cacheSearchEngine.insert("hello");

        // Perform fuzzy search with distance 1
        List<SearchResult> results = cacheSearchEngine.fuzzySearch("helo", 1);

        // Verify the results
        assertEquals("hello", results.get(0).word());
        assertEquals("helo", results.get(1).word());
    }

    @Test
    void fuzzySearchWithNoMatches() {
        // Perform fuzzy search with no expected matches
        List<SearchResult> results = cacheSearchEngine.fuzzySearch("xyz", 1);

        // Verify the results
        assertTrue(results.isEmpty());
    }

    @Test
    void fuzzySearchWithLargerDistance() {
        // Perform fuzzy search with larger distance
        List<SearchResult> results = cacheSearchEngine.fuzzySearch("helo", 2);

        // Verify the results
        assertEquals(3, results.size());
    }

    @Test
    void fuzzySearchWithCache() {
        // Insert "example" and perform fuzzy search to populate cache
        cacheSearchEngine.insert("example");
        cacheSearchEngine.fuzzySearch("exampel", 2);

        // Perform fuzzy search again to use cache
        List<SearchResult> cachedResults = cacheSearchEngine.fuzzySearch("exampel", 2);

        // Verify the results
        assertFalse(cachedResults.isEmpty());
        assertEquals("example", cachedResults.get(0).word());
    }

    @Test
    void insertNullWord() {
        // Insert null word and verify it is not found
        cacheSearchEngine.insert(null);
        assertFalse(cacheSearchEngine.search(null));
    }

    @Test
    void insertEmptyWord() {
        // Insert empty word and verify it is not found
        cacheSearchEngine.insert("");
        assertFalse(cacheSearchEngine.search(""));
    }

    @Test
    void fuzzySearchNullQuery() {
        // Perform fuzzy search with null query
        List<SearchResult> results = cacheSearchEngine.fuzzySearch(null, 1);

        // Verify the results
        assertTrue(results.isEmpty());
    }

    @Test
    void fuzzySearchEmptyQuery() {
        // Perform fuzzy search with empty query
        List<SearchResult> results = cacheSearchEngine.fuzzySearch("", 1);

        // Verify the results
        assertTrue(results.isEmpty());
    }
}