package search;

import core.CacheSearchEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchEngineTest {
    private CacheSearchEngine searchEngine;

    @BeforeEach
    void setUp() {
        searchEngine = new CacheSearchEngine();
        // Add some sample data
        searchEngine.insert("cat");
        searchEngine.insert("car");
        searchEngine.insert("card");
        searchEngine.insert("cart");
    }

    @Test
    void testInsertAndSearch() {
        assertTrue(searchEngine.search("cat"));
        assertTrue(searchEngine.search("car"));
        assertFalse(searchEngine.search("cap"));
    }

    @Test
    void testPrefixSearch() {
        List<String> results = searchEngine.searchWithPrefix("car");
        assertEquals(3, results.size());
        assertTrue(results.contains("car"));
        assertTrue(results.contains("card"));
        assertTrue(results.contains("cart"));
    }

    @Test
    void testEmptyAndNullInputs() {
        assertFalse(searchEngine.search(""));
        assertFalse(searchEngine.search(null));
        assertTrue(searchEngine.searchWithPrefix("").isEmpty());
        assertTrue(searchEngine.searchWithPrefix(null).isEmpty());
    }
}