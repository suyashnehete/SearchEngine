package core;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Represents a node in a Trie data structure.
 */
public class TrieNode {
    private final Map<Character, TrieNode> children;
    private boolean isEndOfWord;
    private String word;
    private int frequency;
    private PriorityQueue<String> topSearches;
    private static final int TOP_SEARCHES_SIZE = 5;

    /**
     * Constructs a new TrieNode with an empty set of children,
     * not marked as the end of a word, and with a frequency of 0.
     */
    public TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
        this.frequency = 0;
        this.topSearches = new PriorityQueue<>();
    }

    /**
     * Returns the children of this TrieNode.
     *
     * @return a map of child characters to TrieNodes
     */
    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    /**
     * Checks if this TrieNode marks the end of a word.
     *
     * @return true if this node is the end of a word, false otherwise
     */
    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    /**
     * Sets whether this TrieNode marks the end of a word.
     *
     * @param endOfWord true if this node should mark the end of a word, false otherwise
     */
    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    /**
     * Returns the word associated with this TrieNode.
     *
     * @return the word associated with this node
     */
    public String getWord() {
        return word;
    }

    /**
     * Sets the word associated with this TrieNode.
     *
     * @param word the word to associate with this node
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Returns the frequency of the word associated with this TrieNode.
     *
     * @return the frequency of the word
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Increments the frequency of the word associated with this TrieNode by one.
     */
    public void incrementFrequency() {
        this.frequency++;
    }

    /**
     * Returns the top searches associated with this TrieNode.
     *
     * @return a priority queue of top searches
     */
    public PriorityQueue<String> getTopSearches() {
        return topSearches;
    }

    /**
     * Adds a word to the top searches associated with this TrieNode.
     * If the number of top searches exceeds the limit, the least frequent search is removed.
     *
     * @param word the word to add to the top searches
     */
    public void addToTopSearches(String word) {
        if (!topSearches.contains(word)) {
            topSearches.offer(word);
            if (topSearches.size() > TOP_SEARCHES_SIZE) {
                topSearches.poll();
            }
        }
    }
}