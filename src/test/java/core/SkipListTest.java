package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SkipListTest {
    private SkipList<Integer> skipList;

    @BeforeEach
    void setUp() {
        skipList = new SkipList<>();
    }

    @Test
    void testInsertAndSearch() {
        skipList.insert(10, 10);
        skipList.insert(20, 20);
        skipList.insert(30, 30);

        assertEquals(10, skipList.search(10), "Value for key 10 should be 10");
        assertEquals(20, skipList.search(20), "Value for key 20 should be 20");
        assertNull(skipList.search(40), "Key 40 should not exist in the skip list");
    }

    @Test
    void testDelete() {
        skipList.insert(15, 15);
        assertTrue(skipList.delete(15), "Key 15 should be successfully deleted");
        assertNull(skipList.search(15), "Key 15 should not exist after deletion");
    }

    @Test
    void testRandomLevel() {
        skipList.insert(5, 5);
        skipList.insert(10, 10);
        skipList.insert(15, 15);

        assertNotNull(skipList.search(10), "Key 10 should exist in the skip list");
        assertNull(skipList.search(20), "Key 20 should not exist in the skip list");
    }
}
