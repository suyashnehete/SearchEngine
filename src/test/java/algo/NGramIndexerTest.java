package algo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NGramIndexerTest {

    private NGramIndexer nGramIndexer;

    @BeforeEach
    void setUp() {
        // Initialize NGramIndexer with n-gram size of 3
        nGramIndexer = new NGramIndexer(3);
    }

    @AfterEach
    void tearDown() {
        // Clean up NGramIndexer instance
        nGramIndexer = null;
    }

    @Test
    void indexAndRetrieveSingleWord() {
        // Index a single word and verify it can be retrieved
        nGramIndexer.indexWord("example");
        Set<String> candidates = nGramIndexer.getCandidates("example");
        assertTrue(candidates.contains("example"));
    }

    @Test
    void indexAndRetrieveMultipleWords() {
        // Index multiple words and verify they can be retrieved
        nGramIndexer.indexWord("example");
        nGramIndexer.indexWord("examine");
        Set<String> candidates = nGramIndexer.getCandidates("exam");
        assertTrue(candidates.contains("example"));
        assertTrue(candidates.contains("examine"));
    }

    @Test
    void retrieveNonIndexedWord() {
        // Verify that a non-indexed word returns an empty set
        Set<String> candidates = nGramIndexer.getCandidates("nonexistent");
        assertTrue(candidates.isEmpty());
    }

    @Test
    void indexAndRetrieveWithSpecialCharacters() {
        // Index a word with special characters and verify it can be retrieved
        nGramIndexer.indexWord("ex@mpl3");
        Set<String> candidates = nGramIndexer.getCandidates("ex@mpl3");
        assertTrue(candidates.contains("ex@mpl3"));
    }

    @Test
    void indexAndRetrieveEmptyString() {
        // Index an empty string and verify it returns an empty set
        nGramIndexer.indexWord("");
        Set<String> candidates = nGramIndexer.getCandidates("");
        assertTrue(candidates.isEmpty());
    }

    @Test
    void indexAndRetrieveNullString() {
        // Index a null string and verify it returns an empty set
        nGramIndexer.indexWord(null);
        Set<String> candidates = nGramIndexer.getCandidates(null);
        assertTrue(candidates.isEmpty());
    }
}
