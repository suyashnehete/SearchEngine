package com.suyash.search_engine_api.query;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.suyash.search_engine_api.index.InvertedIndex;
import com.suyash.search_engine_api.index.InvertedIndexRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryService {
    
    private final InvertedIndexRepository invertedIndexRepository;

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");
    private static final Set<String> STOP_WORDS = Set.of("the", "and", "is", "in", "to", "of", "a", "for");

    public List<Integer> processQuery(String query, int topK) {
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

        return rankDocuments(resultDocIds, termToDocIds, topK);
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
}
