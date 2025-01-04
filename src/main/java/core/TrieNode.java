package core;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class TrieNode {
    private final Map<Character, TrieNode> children;
    private boolean isEndOfWord;
    private String word;
    private int frequency;
    private PriorityQueue<String> topSearches;
    private static final int TOP_SEARCHES_SIZE = 5;

    public TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
        this.frequency = 0;
        this.topSearches = new PriorityQueue<>();
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getFrequency() {
        return frequency;
    }

    public void incrementFrequency() {
        this.frequency++;
    }

    public PriorityQueue<String> getTopSearches() {
        return topSearches;
    }

    public void addToTopSearches(String word) {
        if (!topSearches.contains(word)) {
            topSearches.offer(word);
            if (topSearches.size() > TOP_SEARCHES_SIZE) {
                topSearches.poll();
            }
        }
    }
}
