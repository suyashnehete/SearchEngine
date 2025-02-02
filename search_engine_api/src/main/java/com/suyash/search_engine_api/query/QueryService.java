package com.suyash.search_engine_api.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.suyash.search_engine_api.cache.LRUCacheService;
import com.suyash.search_engine_api.crawler.CrawledPage;
import com.suyash.search_engine_api.crawler.CrawledPageRepository;
import com.suyash.search_engine_api.index.InvertedIndex;
import com.suyash.search_engine_api.index.InvertedIndexRepository;
import com.suyash.search_engine_api.query.utils.Trie;
import com.suyash.search_engine_api.suggestions.utils.EditDistance;
import com.suyash.search_engine_api.suggestions.utils.NGramModel;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryService {

    private final InvertedIndexRepository invertedIndexRepository;
    private final CrawledPageRepository crawledPageRepository;
    private final LRUCacheService<String, List<Integer>> cacheService;

    private Trie queryTrie = new Trie();
    private NGramModel nGramModel = new NGramModel(2);
    private Map<String, List<String>> userSearchHistory = new HashMap<>();

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");
    private static final Set<String> STOP_WORDS = Set.of("the", "and", "is", "in", "to", "of", "a", "for");

    private AtomicInteger queryCount = new AtomicInteger(0);
    private AtomicInteger cacheHitCount = new AtomicInteger(0);

    // Populate Trie with frequent queries from cache
    @PostConstruct
    public void populateTrie() {
        cacheService.asMap().keySet().forEach(queryTrie::insert);
    }

    @PostConstruct
    public void trainNGramModel() {
        List<String> frequentQueries = new ArrayList<>(cacheService.asMap().keySet());
        nGramModel.train(frequentQueries);
    }

    public void logUserQuery(String userId, String query) {
        userSearchHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(query);
    }

    public List<String> getContextAwareSuggestions(String userId, String prefix) {
        List<String> history = userSearchHistory.getOrDefault(userId, Collections.emptyList());
        Map<String, Integer> contextScores = new HashMap<>();

        // Prioritize suggestions based on user history
        for (String pastQuery : history) {
            if (pastQuery.startsWith(prefix)) {
                contextScores.put(pastQuery, contextScores.getOrDefault(pastQuery, 0) + 5); // Higher weight for history
            }
        }

        // Combine with N-gram suggestions
        List<String> nGramSuggestions = nGramModel.getSuggestions(prefix);
        for (String suggestion : nGramSuggestions) {
            contextScores.put(suggestion, contextScores.getOrDefault(suggestion, 0) + 1);
        }

        // Return top suggestions based on context scores
        return contextScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(5) // Limit to top 5 suggestions
                .toList();
    }

    public List<Integer> processQuery(String query, int topK) {

        queryCount.incrementAndGet();

        // Check cache first
        List<Integer> cachedResults = cacheService.getIfPresent(query);
        if (cachedResults != null) {
            System.out.println("Cache hit for query: " + query);
            cacheHitCount.incrementAndGet();
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
        List<Integer> allResults = processQueryWithRanking(query, topK);
        int start = (page - 1) * size;
        int end = Math.min(start + size, allResults.size());
        List<Integer> pagedResults = Collections.emptyList();
        if (start <= end) {
            pagedResults = allResults.subList(start, end);
        }

        List<UrlResponse> urlResponses = pagedResults.stream()
                .map(docId -> {
                    CrawledPage pageObj = crawledPageRepository.findById((long) docId).orElse(null);
                    if (pageObj == null) {
                        return null;
                    }
                    return UrlResponse.builder()
                            .title(pageObj.getTitle())
                            .url(pageObj.getUrl())
                            .shortContent(pageObj.getShortContent())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        return SearchResponse.builder()
                .documents(urlResponses)
                .totalResults(allResults.size())
                .totalPages((int) Math.ceil((double) allResults.size() / size))
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    public SearchResponse processMultipleQuery(List<String> query, int topK, int page, int size) {
        List<Integer> allResults = new ArrayList<>();
        for (String q : query) {
            allResults.addAll(processQueryWithRanking(q, topK / query.size()));
        }
        int start = (page - 1) * size;
        int end = Math.min(start + size, allResults.size());
        List<Integer> pagedResults = Collections.emptyList();
        if (start <= end) {
            pagedResults = allResults.subList(start, end);
        }

        List<UrlResponse> urlResponses = pagedResults.stream()
                .map(docId -> {
                    CrawledPage pageObj = crawledPageRepository.findById((long) docId).orElse(null);
                    if (pageObj == null) {
                        return null;
                    }
                    return UrlResponse.builder()
                            .title(pageObj.getTitle())
                            .url(pageObj.getUrl())
                            .shortContent(pageObj.getShortContent())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        return SearchResponse.builder()
                .documents(urlResponses)
                .totalResults(allResults.size())
                .totalPages((int) Math.ceil((double) allResults.size() / size))
                .currentPage(page)
                .pageSize(size)
                .build();
    }

    public List<Integer> processQueryWithRanking(String query, int topK) {
        // Tokenize and normalize the query
        String[] queryTerms = tokenize(query);

        // Retrieve document IDs for each term
        Map<String, Set<Integer>> termToDocIds = new HashMap<>();
        Map<Integer, Double> docScores = new HashMap<>();

        for (String term : queryTerms) {
            InvertedIndex index = invertedIndexRepository.findByWord(term);
            if (index != null) {
                termToDocIds.put(term, new HashSet<>(index.getDocumentIds()));

                // Calculate frequency-based scores
                for (Integer docId : index.getDocumentIds()) {
                    CrawledPage page = crawledPageRepository.findById((long) docId).orElse(null);
                    if (page != null) {
                        double positionScore = calculatePositionScore(page.getContent(), term);
                        docScores.put(docId, docScores.getOrDefault(docId, 0.0) + positionScore);
                    }
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

        // Rank documents by frequency-based scores
        return frequencyRankedDocuments(resultDocIds, docScores, topK);
    }

    private int countOccurrences(String content, String term) {
        int count = 0;
        int index = 0;
        while ((index = content.indexOf(term, index)) != -1) {
            count++;
            index += term.length();
        }
        return count;
    }

    private List<Integer> frequencyRankedDocuments(Set<Integer> docIds, Map<Integer, Double> docScores, int topK) {
        return docIds.stream()
                .sorted((id1, id2) -> {
                    double score1 = docScores.getOrDefault(id1, 0.0)
                            + crawledPageRepository.findById((long) id1).orElse(null).getPageRankScore();
                    double score2 = docScores.getOrDefault(id2, 0.0)
                            + crawledPageRepository.findById((long) id2).orElse(null).getPageRankScore();
                    return Double.compare(score2, score1);
                })
                .limit(topK)
                .toList();
    }

    private String[] tokenize(String text) {
        return WORD_PATTERN.matcher(text.toLowerCase()).results()
                .map(match -> match.group())
                .toArray(String[]::new);
    }

    private double calculatePositionScore(String content, String term) {
        int index = content.toLowerCase().indexOf(term.toLowerCase());
        if (index == -1) {
            return 0.0; // Term not found
        }
        // Higher score for terms appearing earlier in the content
        return 1.0 / (index + 1);
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

    public List<String> getSuggestionsTrie(String prefix) {
        return queryTrie.getSuggestions(prefix);
    }

    public List<String> getSuggestionsNGram(String prefix) {
        return nGramModel.getSuggestions(prefix);
    }

    public List<Integer> processQueryWithFilters(String query, List<String> tags, int topK) {
        List<Integer> results = processQueryWithRanking(query, topK);

        if (tags == null || tags.isEmpty()) {
            return results;
        }

        return results.stream()
                .filter(docId -> {
                    CrawledPage page = crawledPageRepository.findById((long) docId).orElse(null);
                    return page != null && page.getTags().containsAll(tags);
                })
                .toList();
    }

    public int getQueryCount() {
        return queryCount.get();
    }

    public double getCacheHitRate() {
        return queryCount.get() == 0 ? 0 : (double) cacheHitCount.get() / queryCount.get();
    }

    public SearchResponse processQueryWithCorrections(String query, int topK, int page, int size) {
        SearchResponse results = processQuery(query, topK, page, size);

        if (!results.documents().isEmpty()) {
            return results;
        }

        return suggestCorrections(query, topK, page, size);
    }

    private SearchResponse suggestCorrections(String query, int topK, int page, int size) {
        List<String> allQueries = new ArrayList<>(cacheService.asMap().keySet());
        Map<String, Integer> corrections = new HashMap<>();

        for (String cachedQuery : allQueries) {
            int distance = EditDistance.calculate(query, cachedQuery);
            if (distance <= query.length() / 3) {
                corrections.put(cachedQuery, distance);
            }
        }

        List<String> topQueries = corrections.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue())
                .limit(topK)
                .map(entry -> {
                    return entry.getKey();
                })
                .toList();
        return processMultipleQuery(topQueries, topK, page, size);
    }

}
