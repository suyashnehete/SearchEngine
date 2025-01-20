package core;

/**
 * A node in a SkipList, which holds a key-value pair and references to forward nodes at different levels.
 *
 * @param <T> the type of the value held in this node
 */
public class SkipListNode<T> {
    private final T value;
    private final int key;
    private SkipListNode<T>[] forward;

    /**
     * Constructs a SkipListNode with the specified key, value, and level.
     *
     * @param key the key of the node
     * @param value the value of the node
     * @param level the level of the node
     */
    public SkipListNode(int key, T value, int level) {
        this.key = key;
        this.value = value;
        this.forward = new SkipListNode[level + 1];
    }

    /**
     * Returns the key of the node.
     *
     * @return the key of the node
     */
    public int getKey() {
        return key;
    }

    /**
     * Returns the value of the node.
     *
     * @return the value of the node
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the forward references of the node.
     *
     * @return an array of forward references
     */
    public SkipListNode<T>[] getForward() {
        return forward;
    }
}