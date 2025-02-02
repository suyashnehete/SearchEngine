package com.suyash.search_engine_api.query;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.suyash.search_engine_api.cache.LRUCacheService;
import com.suyash.search_engine_api.index.InvertedIndex;
import com.suyash.search_engine_api.index.InvertedIndexRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryService {

    private final InvertedIndexRepository invertedIndexRepository;
    private final LRUCacheService<String, List<Integer>> cacheService;

    private Trie queryTrie = new Trie();

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");
    private static final Set<String> STOP_WORDS = Set.of("the", "and", "is", "in", "to", "of", "a", "for");

    // Populate Trie with frequent queries from cache
    @PostConstruct
    public void populateTrie() {
        cacheService.asMap().keySet().forEach(queryTrie::insert);
    }

    public List<Integer> processQuery(String query, int topK) {

        // Check cache first
        List<Integer> cachedResults = cacheService.getIfPresent(query);
        if (cachedResults != null) {
            System.out.println("Cache hit for query: " + query);
            return cachedResults;
        }

        // Tokenize and normalize the query
        String[] queryTerms = tokenize(query);

        // Retrieve document IDs for each term
        Map<String, Set<Integer>> termToDocIds = new HashMap<>();
        for (String term : queryTerms) {
            if (!STOP_WORDS.contains(term)) {
                InvertedIndex index = invertedIndexRepository.findByWord(term);
                if (index != null) {
                    termToDocIds.put(term, new HashSet<>(index.getDocumentIds()));
                }
            }
        }

        // Combine results using Boolean AND logic
        Set<Integer> resultDocIds = null;
        for (Set<Integer> docIds : termToDocIds.values()) {
            if (resultDocIds == null) {
                resultDocIds = new HashSet<>(docIds);
            } else {
                resultDocIds.retainAll(docIds); // Intersection of sets
            }
        }

        // Rank documents by relevance (simple frequency-based ranking)
        if (resultDocIds == null || resultDocIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Rank documents by relevance
        List<Integer> rankedResults = rankDocuments(resultDocIds, termToDocIds, topK);

        // Store results in cache
        cacheService.put(query, rankedResults);

        return rankedResults;
    }

    public SearchResponse processQuery(String query, int topK, int page, int size) {
        List<Integer> allResults = processQuery(query, topK);
        int start = (page - 1) * size;
        int end = Math.min(start + size, allResults.size());
        List<Integer> pagedResults = Collections.emptyList();
        if(start <= end){
            pagedResults = allResults.subList(start, end);
        }
        return SearchResponse.builder()
                .documentIds(pagedResults)
                .totalResults(allResults.size())
                .totalPages((int) Math.ceil((double) allResults.size() / size))
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    private String[] tokenize(String text) {
        return WORD_PATTERN.matcher(text.toLowerCase()).results()
                .map(match -> match.group())
                .toArray(String[]::new);
    }

    private List<Integer> rankDocuments(Set<Integer> docIds, Map<String, Set<Integer>> termToDocIds, int topK) {
        // Simple ranking based on term frequency
        Map<Integer, Integer> docScores = new HashMap<>();
        for (Integer docId : docIds) {
            int score = 0;
            for (Set<Integer> docList : termToDocIds.values()) {
                if (docList.contains(docId)) {
                    score++;
                }
            }
            docScores.put(docId, score);
        }

        // Sort documents by score in descending order
        return docScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(topK)
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<String> getSuggestions(String prefix) {
        return queryTrie.getSuggestions(prefix);
    }
}
