package search;

import core.BloomFilter;
import core.SkipList;
import core.TrieNode;

import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SuperSearchEngine is an advanced search engine that uses a BloomFilter and SkipList
 * to manage and search for words and their associated document IDs.
 */
public class SuperSearchEngine extends CacheSearchEngine {

    private final BloomFilter bloomFilter;
    private final int[] hashSeeds = {3, 7, 11, 17};
    private final Map<String, SkipList<Integer>> postingLists;

    /**
     * Constructs a SuperSearchEngine with a BloomFilter and a ConcurrentHashMap for posting lists.
     */
    public SuperSearchEngine() {
        super();
        this.bloomFilter = new BloomFilter(10000, hashSeeds);
        this.postingLists = new ConcurrentHashMap<>();
    }

    /**
     * Inserts a word into the search engine.
     *
     * @param word the word to insert
     */
    @Override
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }

        String normalizedWord = word.toLowerCase();
        bloomFilter.add(normalizedWord);
        lock.writeLock().lock();
        try {
            super.insert(normalizedWord);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Adds a document ID to the posting list of the specified word.
     *
     * @param word the word to associate with the document ID
     * @param docId the document ID to add
     */
    public void addDocument(String word, int docId) {
        if (word == null || word.isEmpty()) {
            return;
        }
        String normalizedWord = word.toLowerCase();
        lock.writeLock().lock();
        try {
            SkipList<Integer> postingList = postingLists.computeIfAbsent(normalizedWord, k -> new SkipList<>());
            postingList.insert(docId, docId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves the posting list of document IDs for the specified word using TrieNode.
     *
     * @param word the word to search for
     * @return a NavigableSet of document IDs, or null if the word is not found
     */
    public NavigableSet<Integer> getPostingListV1(String word) {
        if (word == null || word.isEmpty()) {
            return null;
        }

        lock.readLock().lock();
        try {
            TrieNode node = searchNode(word.toLowerCase());
            return node != null ? node.getPostingList() : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Retrieves the posting list of document IDs for the specified word using SkipList.
     *
     * @param word the word to search for
     * @return a SkipList of document IDs, or null if the word is not found
     */
    public SkipList<Integer> getPostingList(String word) {
        if (word == null || word.isEmpty()) {
            return null;
        }

        lock.readLock().lock();
        try {
            return postingLists.get(word.toLowerCase());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Checks if the specified word might be contained in the BloomFilter.
     *
     * @param word the word to check
     * @return true if the word might be in the BloomFilter, false otherwise
     */
    public boolean mightContain(String word) {
        return word != null && bloomFilter.mightContain(word.toLowerCase());
    }

    /**
     * Removes a document ID from the posting list of the specified word.
     *
     * @param word the word to disassociate with the document ID
     * @param docId the document ID to remove
     * @return true if the document ID was found and removed, false otherwise
     */
    public boolean removeDocument(String word, int docId) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        String normalizedWord = word.toLowerCase();
        lock.writeLock().lock();
        try {
            SkipList<Integer> postingList = postingLists.get(normalizedWord);
            if (postingList != null) {
                return postingList.delete(docId);
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}