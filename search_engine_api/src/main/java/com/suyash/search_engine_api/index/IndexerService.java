package com.suyash.search_engine_api.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.suyash.search_engine_api.crawler.CrawledPage;
import com.suyash.search_engine_api.crawler.CrawledPageRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class IndexerService {

    private final CrawledPageRepository crawledPageRepository;
    private final InvertedIndexRepository invertedIndexRepository;

    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");
    private static final Set<String> STOP_WORDS = Set.of("the", "and", "is", "in", "to", "of", "a", "for");

    public void buildIndex() {
        List<CrawledPage> pages = crawledPageRepository.findAll();
        Map<String, Map<Integer, Integer>> termFrequencyMap = new HashMap<>();
        Map<String, Integer> documentFrequencyMap = new HashMap<>();

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
        }

        // Compute TF-IDF and save to database
        saveTfIdfToDatabase(termFrequencyMap, documentFrequencyMap, pages.size());
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

    // private void saveToDatabase(Map<String, Set<Integer>> invertedIndexMap) {
    //     for (Map.Entry<String, Set<Integer>> entry : invertedIndexMap.entrySet()) {
    //         String word = entry.getKey();
    //         List<Integer> documentIds = new ArrayList<>(entry.getValue());

    //         InvertedIndex index = invertedIndexRepository.findByWord(word);
    //         if (index == null) {
    //             index = InvertedIndex.builder()
    //                     .word(word)
    //                     .documentIds(documentIds)
    //                     .build();
    //         } else {
    //             index.getDocumentIds().addAll(documentIds);
    //         }
    //         invertedIndexRepository.save(index);
    //     }
    // }

}
