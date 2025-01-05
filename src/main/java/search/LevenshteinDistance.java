package search;

public class LevenshteinDistance {

    /**
     * Calculates the Levenshtein distance between two words.
     *
     * @param word1 the first word
     * @param word2 the second word
     * @return the Levenshtein distance between word1 and word2
     */
    public static int calculate(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        // Create a matrix to store the distances
        int[][] dp = new int[m + 1][n + 1];

        // Initialize first row and column
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Fill the matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j - 1], // Replace
                            Math.min(
                                    dp[i - 1][j],   // Delete
                                    dp[i][j - 1]    // Insert
                            )
                    );
                }
            }
        }

        return dp[m][n];
    }

    /**
     * Checks if the Levenshtein distance between two words is within a given maximum distance.
     *
     * @param word1 the first word
     * @param word2 the second word
     * @param maxDistance the maximum allowed Levenshtein distance
     * @return true if the distance is within maxDistance, false otherwise
     */
    public static boolean isWithinDistance(String word1, String word2, int maxDistance) {
        // Length checks can help early termination
        if (Math.abs(word1.length() - word2.length()) > maxDistance) {
            return false;
        }

        // For very short strings, use the standard algorithm
        if (word1.length() <= maxDistance || word2.length() <= maxDistance) {
            return calculate(word1, word2) <= maxDistance;
        }

        // Use row-by-row approach with early termination
        int[] previousRow = new int[word2.length() + 1];
        int[] currentRow = new int[word2.length() + 1];

        // Initialize the first row
        for (int j = 0; j <= word2.length(); j++) {
            previousRow[j] = j;
        }

        // Process each row
        for (int i = 1; i <= word1.length(); i++) {
            currentRow[0] = i;
            int minimumInRow = i;

            for (int j = 1; j <= word2.length(); j++) {
                int cost = (word1.charAt(i - 1) == word2.charAt(j - 1)) ? 0 : 1;

                currentRow[j] = Math.min(
                        Math.min(currentRow[j - 1] + 1, previousRow[j] + 1),
                        previousRow[j - 1] + cost
                );

                minimumInRow = Math.min(minimumInRow, currentRow[j]);
            }

            // If entire row's minimum exceeds maxDistance, it's too different
            if (minimumInRow > maxDistance) {
                return false;
            }

            // Swap rows
            int[] temp = previousRow;
            previousRow = currentRow;
            currentRow = temp;
        }

        return previousRow[word2.length()] <= maxDistance;
    }
}