package com.suyash.se.indexer.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.suyash.se.indexer.crawler.CrawledPage;
import com.suyash.se.indexer.crawler.CrawlerClient;
import com.suyash.se.indexer.utils.PageRank;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class IndexerService {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");
    private static final Set<String> STOP_WORDS = Set.of("the", "and", "is", "in", "to", "of", "a", "for");
    private final InvertedIndexRepository invertedIndexRepository;
    private final CrawlerClient crawlerClient;

    public void buildIndex(List<CrawledPage> pages) {
        Map<String, Map<Integer, Integer>> termFrequencyMap = new HashMap<>();
        Map<String, Integer> documentFrequencyMap = new HashMap<>();

        // Build adjacency list for links between pages
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        for (CrawledPage page : pages) {
            int docId = Math.toIntExact(page.getId());
            String content = page.getContent();

            // Tokenize and normalize the text
            String[] words = tokenize(content);

            Set<String> uniqueWords = new HashSet<>();
            for (String word : words) {
                if (!STOP_WORDS.contains(word)) {
                    uniqueWords.add(word);

                    // Update term frequency
                    termFrequencyMap.computeIfAbsent(word, k -> new HashMap<>())
                            .merge(docId, 1, Integer::sum);
                }
            }

            // Update document frequency
            for (String word : uniqueWords) {
                documentFrequencyMap.merge(word, 1, Integer::sum);
            }

            // Extract links and build adjacency list
            Document doc = Jsoup.parse(content);
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                CrawledPage linkedPage = crawlerClient.findByUrl(nextUrl);
                if (linkedPage != null) {
                    int linkedDocId = Math.toIntExact(linkedPage.getId());
                    adjacencyList.computeIfAbsent(docId, k -> new ArrayList<>()).add(linkedDocId);
                }
            }
        }

        // Compute TF-IDF and save to database
        saveTfIdfToDatabase(termFrequencyMap, documentFrequencyMap, pages.size());

        // Compute PageRank and save to database
        Map<Integer, Double> pageRankScores = PageRank.calculate(adjacencyList);
        for (Map.Entry<Integer, Double> entry : pageRankScores.entrySet()) {
            CrawledPage page = crawlerClient.findById((long) entry.getKey()).orElse(null);
            if (page != null) {
                page.setPageRankScore(entry.getValue());
                crawlerClient.save(page);
            }
        }
    }

    private String[] tokenize(String text) {
        return WORD_PATTERN.matcher(text.toLowerCase()).results()
                .map(match -> match.group())
                .toArray(String[]::new);
    }

    private void saveTfIdfToDatabase(Map<String, Map<Integer, Integer>> termFrequencyMap,
            Map<String, Integer> documentFrequencyMap, int totalDocuments) {
        for (Map.Entry<String, Map<Integer, Integer>> entry : termFrequencyMap.entrySet()) {
            String word = entry.getKey();
            Map<Integer, Integer> tfMap = entry.getValue();
            int df = documentFrequencyMap.getOrDefault(word, 1);

            Map<Integer, Double> tfidfScores = new HashMap<>();
            for (Map.Entry<Integer, Integer> docEntry : tfMap.entrySet()) {
                int docId = docEntry.getKey();
                int tf = docEntry.getValue();
                double idf = Math.log((double) totalDocuments / df);
                double tfIdf = tf * idf;
                tfidfScores.put(docId, tfIdf);
            }

            // Save TF-IDF scores to database
            InvertedIndex index = invertedIndexRepository.findByWord(word);
            if (index == null) {
                index = InvertedIndex.builder()
                        .word(word)
                        .documentIds(new ArrayList<>(tfidfScores.keySet()))
                        .tfidfScores(tfidfScores)
                        .build();
            } else {
                index.getDocumentIds().addAll(tfidfScores.keySet());
                index.getTfidfScores().putAll(tfidfScores);
            }
            invertedIndexRepository.save(index);
        }
    }

    // Admin operations
    public void reindexAll() {
        // Clear existing index
        clearIndex();
        
        // Fetch all crawled pages and rebuild index
        List<CrawledPage> allPages = crawlerClient.findAllPages();
        if (!allPages.isEmpty()) {
            buildIndex(allPages);
        }
    }

    public void optimizeIndex() {
        // Remove duplicate entries and optimize storage
        List<InvertedIndex> allIndices = invertedIndexRepository.findAll();
        for (InvertedIndex index : allIndices) {
            // Remove duplicates from document IDs
            Set<Integer> uniqueDocIds = new HashSet<>(index.getDocumentIds());
            index.setDocumentIds(new ArrayList<>(uniqueDocIds));
            invertedIndexRepository.save(index);
        }
    }

    public void clearIndex() {
        // Delete all index entries
        invertedIndexRepository.deleteAll();
    }

    public Map<String, Object> getIndexStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalTerms = invertedIndexRepository.count();
        List<InvertedIndex> allIndices = invertedIndexRepository.findAll();

        int totalDocuments = allIndices.stream()
                .mapToInt(index -> index.getDocumentIds().size())
                .sum();

        stats.put("totalTerms", totalTerms);
        stats.put("totalDocuments", totalDocuments);
        stats.put("averageDocumentsPerTerm", totalTerms > 0 ? (double) totalDocuments / totalTerms : 0);

        return stats;
    }

}
