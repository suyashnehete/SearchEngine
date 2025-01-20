package search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NavigableSet;

import static org.junit.jupiter.api.Assertions.*;

public class SuperSearchEngineTest {

    private SuperSearchEngine searchEngine;

    @BeforeEach
    void setUp() {
        searchEngine = new SuperSearchEngine();
    }

    @Test
    void testInsertAndSearch() {
        searchEngine.insert("hello");
        searchEngine.insert("world");

        assertTrue(searchEngine.search("hello"), "Word 'hello' should be found");
        assertTrue(searchEngine.search("world"), "Word 'world' should be found");
        assertFalse(searchEngine.search("test"), "Word 'test' should not be found");
    }

    @Test
    void testBloomFilterIntegration() {
        searchEngine.insert("example");
        searchEngine.insert("query");

        assertTrue(searchEngine.mightContain("example"), "Bloom Filter should recognize 'example'");
        assertTrue(searchEngine.mightContain("query"), "Bloom Filter should recognize 'query'");
        assertFalse(searchEngine.mightContain("random"), "Bloom Filter should not recognize 'random'");
    }

    @Test
    void testPostingList() {
        searchEngine.insert("document");
        searchEngine.addDocument("document", 1);
        searchEngine.addDocument("document", 2);
        searchEngine.addDocument("document", 3);

        NavigableSet<Integer> postingList = searchEngine.getPostingListV1("document");
        assertNotNull(postingList, "Posting list for 'document' should not be null");
        assertEquals(3, postingList.size(), "Posting list should contain 3 document IDs");
        assertTrue(postingList.contains(1), "Posting list should contain document ID 1");
        assertTrue(postingList.contains(2), "Posting list should contain document ID 2");
        assertTrue(postingList.contains(3), "Posting list should contain document ID 3");
    }

    @Test
    void testCacheIntegration() {
        searchEngine.insert("cache");
        searchEngine.fuzzySearch("cache", 1);

        // Perform the search again to test cache retrieval
        var results = searchEngine.fuzzySearch("cache", 1);
        assertNotNull(results, "Search results should not be null");
        assertFalse(results.isEmpty(), "Search results should not be empty");
        assertEquals("cache", results.get(0).word(), "First result should match the query word");
    }

    @Test
    void testAddDocumentForNonExistentWord() {
        searchEngine.addDocument("missing", 1);
        NavigableSet<Integer> postingList = searchEngine.getPostingListV1("missing");

        assertNull(postingList, "Posting list for non-existent word should be null");
    }

    @Test
    void testConcurrency() throws InterruptedException {
        Thread thread1 = new Thread(() -> searchEngine.insert("concurrent1"));
        Thread thread2 = new Thread(() -> searchEngine.insert("concurrent2"));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        assertTrue(searchEngine.search("concurrent1"), "Word 'concurrent1' should be found");
        assertTrue(searchEngine.search("concurrent2"), "Word 'concurrent2' should be found");
    }

}
