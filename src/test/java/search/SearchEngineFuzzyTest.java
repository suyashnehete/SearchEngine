package search;


import core.SearchResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test class for the SearchEngine fuzzy search functionality.
 */
class SearchEngineFuzzyTest {

    private SearchEngine searchEngine;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        searchEngine = new SearchEngine();
        // Add sample data with some similar words
        searchEngine.insert("hello");
        searchEngine.insert("helo");
        searchEngine.insert("help");
        searchEngine.insert("helicopter");
        searchEngine.insert("world");
    }

    @org.junit.jupiter.api.Test
    void testfuzzySearch() {
        List<SearchResult> results = searchEngine.fuzzySearch("helo", 1);

        // "helo" -> distance 0
        // "hello" -> distance 1
        // "help" -> distance 1
        // Expecting all three to appear
        assertEquals(3, results.size());
    }

    @org.junit.jupiter.api.Test
    void testfuzzySearchWithFrequency() {
        // Insert "hello" multiple times to boost its frequency
        searchEngine.insert("hello");
        searchEngine.insert("hello");

        // Now "hello" should have frequency=3, while "helo" has frequency=1
        java.util.List<SearchResult> results = searchEngine.fuzzySearch("helo", 1);

        // Because sorting is set to prioritize frequency desc, "hello" appears before "helo".
        assertEquals("hello", results.get(0).word());
        assertEquals("helo", results.get(1).word());
    }

    @org.junit.jupiter.api.Test
    void testfuzzySearchWithNoMatches() {
        java.util.List<SearchResult> results = searchEngine.fuzzySearch("xyz", 1);
        org.junit.jupiter.api.Assertions.assertTrue(results.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void testfuzzySearchWithLargerDistance() {
        // With a max distance of 2, words like "help" (2 edits away from "helo") should appear
        java.util.List<SearchResult> results = searchEngine.fuzzySearch("helo", 2);
        // "helo", "hello", and "help" all appear within distance=2
        assertEquals(3, results.size());
    }
}

