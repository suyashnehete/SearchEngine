package core;

/**
 * A record that represents the result of a search operation.
 *
 * @param word the word found in the search
 * @param distance the Levenshtein distance between the search query and the word
 * @param frequency the frequency of the word in the Trie
 */
public record SearchResult(String word, int distance, int frequency) {
    /**
     * Returns a string representation of the SearchResult.
     *
     * @return a formatted string containing the word, distance, and frequency
     */
    @Override
    public String toString() {
        return String.format("word='%s', distance=%d, frequency=%d", word, distance, frequency);
    }
}