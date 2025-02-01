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
        Map<String, Set<Integer>> invertedIndexMap = new HashMap<>();

        for (CrawledPage page : pages) {
            int docId = Math.toIntExact(page.getId());
            String content = page.getContent();

            // Tokenize and normalize the text
            String[] words = tokenize(content);

            for (String word : words) {
                if (!STOP_WORDS.contains(word)) {
                    invertedIndexMap.computeIfAbsent(word, k -> new HashSet<>()).add(docId);
                }
            }
        }

        // Save the inverted index to the database
        saveToDatabase(invertedIndexMap);
    }

    private String[] tokenize(String text) {
        return WORD_PATTERN.matcher(text.toLowerCase()).results()
                .map(match -> match.group())
                .toArray(String[]::new);
    }

    private void saveToDatabase(Map<String, Set<Integer>> invertedIndexMap) {
        for (Map.Entry<String, Set<Integer>> entry : invertedIndexMap.entrySet()) {
            String word = entry.getKey();
            List<Integer> documentIds = new ArrayList<>(entry.getValue());

            InvertedIndex index = invertedIndexRepository.findByWord(word);
            if (index == null) {
                index = InvertedIndex.builder()
                        .word(word)
                        .documentIds(documentIds)
                        .build();
            } else {
                index.getDocumentIds().addAll(documentIds);
            }
            invertedIndexRepository.save(index);
        }
    }
}
