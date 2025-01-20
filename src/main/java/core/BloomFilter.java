package core;

import java.util.BitSet;

/**
 * A BloomFilter is a probabilistic data structure that is used to test whether an element is a member of a set.
 * It can return false positives but never false negatives.
 */
public class BloomFilter {
    private final BitSet bitSet;
    private final int size;
    private final int[] hashSeeds;

    /**
     * Constructs a BloomFilter with the specified size and hash seeds.
     *
     * @param size the size of the BloomFilter
     * @param hashSeeds an array of integers used as seeds for the hash functions
     */
    public BloomFilter(int size, int[] hashSeeds) {
        this.bitSet = new BitSet(size);
        this.size = size;
        this.hashSeeds = hashSeeds;
    }

    /**
     * Hashes a word using a specified seed.
     *
     * @param word the word to hash
     * @param seed the seed to use for hashing
     * @return the hash value
     */
    private int hash(String word, int seed) {
        int hash = 0;
        for (char c : word.toCharArray()) {
            hash = seed * hash + c;
        }
        return (hash & 0x7fffffff) % size;
    }

    /**
     * Adds a word to the BloomFilter.
     *
     * @param word the word to add
     */
    public void add(String word) {
        for (int seed : hashSeeds) {
            int hash = hash(word, seed);
            bitSet.set(hash);
        }
    }

    /**
     * Checks if a word might be contained in the BloomFilter.
     *
     * @param word the word to check
     * @return true if the word might be in the BloomFilter, false otherwise
     */
    public boolean mightContain(String word) {
        for (int seed : hashSeeds) {
            int hash = hash(word, seed);
            if (!bitSet.get(hash)) {
                return false;
            }
        }
        return true;
    }
}