import org.junit.jupiter.api.Test;
import search.LevenshteinDistance;

import static org.junit.jupiter.api.Assertions.*;

public class LevenshteinDistanceTest {

    @Test
    void testExactMatch() {
        assertEquals(0, LevenshteinDistance.calculate("hello", "hello"));
    }

    @Test
    void testSingleCharacterDifference() {
        // "hello" -> "helo" differs by removing one 'l' => distance = 1
        assertEquals(1, LevenshteinDistance.calculate("hello", "helo"));

        // "hello" -> "hellw" differs by substituting 'o' with 'w' => distance = 1
        assertEquals(1, LevenshteinDistance.calculate("hello", "hellw"));
    }

    @Test
    void testMultipleCharacterDifferences() {
        // "hello" -> "hel" has two deletions => distance = 2
        assertEquals(2, LevenshteinDistance.calculate("hello", "hel"));

        // "hello" -> "help" =>
        // 1) replace 'l' with 'p'
        // 2) remove 'o'
        // => distance = 2
        assertEquals(2, LevenshteinDistance.calculate("hello", "help"));
    }

    @Test
    void testEmptyStrings() {
        // "hello" -> "" is removing 5 characters => distance = 5
        assertEquals(5, LevenshteinDistance.calculate("hello", ""));

        // "" -> "" => distance = 0
        assertEquals(0, LevenshteinDistance.calculate("", ""));
    }

    @Test
    void testIsWithinDistance() {
        // "hello" -> "helo" => distance = 1, within 1
        assertTrue(LevenshteinDistance.isWithinDistance("hello", "helo", 1));

        // "hello" -> "help" => distance = 2, so it is within 2 -> changed to assertTrue
        assertTrue(LevenshteinDistance.isWithinDistance("hello", "help", 2));

        // "algorithm" -> "algorithms" => distance = 1, within 1
        assertTrue(LevenshteinDistance.isWithinDistance("algorithm", "algorithms", 1));
    }
}