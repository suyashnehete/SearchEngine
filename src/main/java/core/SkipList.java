package core;

import java.util.Random;

/**
 * A SkipList is a data structure that allows fast search, insertion, and deletion operations.
 *
 * @param <T> the type of elements held in this SkipList
 */
public class SkipList<T> {
    private static final double P = 0.5;
    private static final int MAX_LEVEL = 16;

    private final SkipListNode<T> head;
    private final Random random;
    private int level;

    /**
     * Constructs an empty SkipList.
     */
    public SkipList() {
        this.head = new SkipListNode<>(Integer.MIN_VALUE, null, MAX_LEVEL);
        this.random = new Random();
        this.level = 0;
    }

    /**
     * Generates a random level for a new node.
     *
     * @return the random level
     */
    private int randomLevel() {
        int lvl = 0;
        while (lvl < MAX_LEVEL && random.nextDouble() < P) {
            lvl++;
        }
        return lvl;
    }

    /**
     * Inserts a key-value pair into the SkipList.
     *
     * @param key the key to insert
     * @param value the value associated with the key
     */
    public void insert(int key, T value) {
        SkipListNode<T>[] update = new SkipListNode[MAX_LEVEL + 1];
        SkipListNode<T> current = head;

        for (int i = level; i >= 0; i--) {
            while (current.getForward()[i] != null && current.getForward()[i].getKey() < key) {
                current = current.getForward()[i];
            }
            update[i] = current;
        }

        current = current.getForward()[0];

        if (current == null || current.getKey() != key) {
            int newLevel = randomLevel();
            if (newLevel > level) {
                for (int i = level + 1; i <= newLevel; i++) {
                    update[i] = head;
                }
                level = newLevel;
            }

            SkipListNode<T> newNode = new SkipListNode<>(key, value, newLevel);
            for (int i = 0; i <= newLevel; i++) {
                newNode.getForward()[i] = update[i].getForward()[i];
                update[i].getForward()[i] = newNode;
            }
        } else {
            current.getValue();
        }
    }

    /**
     * Searches for a value associated with the given key in the SkipList.
     *
     * @param key the key to search for
     * @return the value associated with the key, or null if the key is not found
     */
    public T search(int key) {
        SkipListNode<T> current = head;

        for (int i = level; i >= 0; i--) {
            while (current.getForward()[i] != null && current.getForward()[i].getKey() < key) {
                current = current.getForward()[i];
            }
        }

        current = current.getForward()[0];
        if (current != null && current.getKey() == key) {
            return current.getValue();
        }

        return null;
    }

    /**
     * Deletes a key-value pair from the SkipList.
     *
     * @param key the key to delete
     * @return true if the key was found and deleted, false otherwise
     */
    public boolean delete(int key) {
        SkipListNode<T>[] update = new SkipListNode[MAX_LEVEL + 1];
        SkipListNode<T> current = head;

        for (int i = level; i >= 0; i--) {
            while (current.getForward()[i] != null && current.getForward()[i].getKey() < key) {
                current = current.getForward()[i];
            }
            update[i] = current;
        }

        current = current.getForward()[0];

        if (current != null && current.getKey() == key) {
            for (int i = 0; i <= level; i++) {
                if (update[i].getForward()[i] != current) {
                    break;
                }
                update[i].getForward()[i] = current.getForward()[i];
            }

            while (level > 0 && head.getForward()[level] == null) {
                level--;
            }
            return true;
        }
        return false;
    }
}