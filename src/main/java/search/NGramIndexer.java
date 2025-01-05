package search;

import java.util.*;

/**
 * An NGramIndexer indexes words by their n-grams and allows querying for candidate words based on n-grams.
 */
public class NGramIndexer {
    private final Map<String, Set<String>> nGramIndex;
    private final int nGramSize;

    /**
     * Constructs an NGramIndexer with the specified n-gram size.
     *
     * @param nGramSize the size of the n-grams to be generated
     */
    public NGramIndexer(int nGramSize) {
        this.nGramSize = nGramSize;
        this.nGramIndex = new HashMap<>();
    }

    /**
     * Indexes the specified word by generating its n-grams and adding them to the index.
     *
     * @param word the word to be indexed
     */
    public void indexWord(String word) {
        Set<String> nGrams = generateNGrams(word);
        for (String nGram : nGrams) {
            nGramIndex.computeIfAbsent(nGram, k -> new HashSet<>()).add(word);
        }
    }

    /**
     * Retrieves a set of candidate words that match the n-grams of the specified query.
     *
     * @param query the query string to find candidate words for
     * @return a set of candidate words that match the n-grams of the query
     */
    public Set<String> getCandidates(String query) {
        Set<String> candidates = new HashSet<>();
        if(query == null || query.isEmpty()) return candidates;
        Set<String> queryNGrams = generateNGrams(query);

        for (String nGram : queryNGrams) {
            Set<String> words = nGramIndex.getOrDefault(nGram, Collections.emptySet());
            candidates.addAll(words);
        }

        return candidates;
    }

    /**
     * Generates a set of n-grams for the specified word.
     *
     * @param word the word to generate n-grams for
     * @return a set of n-grams for the word
     */
    private Set<String> generateNGrams(String word) {
        Set<String> nGrams = new HashSet<>();
        String paddedWord = "$" + word + "$";
        for (int i = 0; i <= paddedWord.length() - nGramSize; i++) {
            nGrams.add(paddedWord.substring(i, i + nGramSize));
        }
        return nGrams;
    }
}