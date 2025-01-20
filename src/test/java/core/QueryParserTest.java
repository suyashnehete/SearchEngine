package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryParserTest {
    private QueryParser parser;

    @BeforeEach
    void setUp() {
        parser = new QueryParser();
    }

    @Test
    void testSimpleQuery() {
        Query query = parser.parse("search engine");
        assertEquals(2, query.tokens().size());
        assertTrue(query.tokens().contains("search"));
        assertTrue(query.tokens().contains("engine"));
    }

    @Test
    void testBooleanQuery() {
        Query query = parser.parse("search AND engine OR algorithm");
        assertEquals(3, query.tokens().size());
        assertTrue(query.operators().contains("AND"));
        assertTrue(query.operators().contains("OR"));
    }

    @Test
    void testPhraseQuery() {
        Query query = parser.parse("\"exact phrase\" search engine");
        assertEquals(1, query.phrases().size());
        assertTrue(query.phrases().contains("exact phrase"));
    }

    @Test
    void testWildcardQuery() {
        Query query = parser.parse("sear* eng*ine");
        assertEquals(2, query.wildcards().size());
        assertTrue(query.wildcards().contains("sear*"));
        assertTrue(query.wildcards().contains("eng*ine"));
    }

    @Test
    void testComplexQuery() {
        Query query = parser.parse("\"exact phrase\" AND algor* OR \"another phrase\" NOT irrelevant");
        assertEquals(1, query.wildcards().size());
        assertTrue(query.wildcards().contains("algor*"));

        assertEquals(2, query.phrases().size());
        assertTrue(query.phrases().contains("exact phrase"));
        assertTrue(query.phrases().contains("another phrase"));

        assertEquals(1, query.tokens().size());
        assertTrue(query.tokens().contains("irrelevant"));

        assertEquals(3, query.operators().size());
        assertTrue(query.operators().contains("AND"));
        assertTrue(query.operators().contains("OR"));
        assertTrue(query.operators().contains("NOT"));
    }
}
