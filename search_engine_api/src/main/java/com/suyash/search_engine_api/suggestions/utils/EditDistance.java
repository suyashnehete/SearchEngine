package com.suyash.search_engine_api.suggestions.utils;

public class EditDistance {
    public static int calculate(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();

        // Create a DP table
        int[][] dp = new int[m + 1][n + 1];

        // Initialize the table
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Fill the table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No operation needed
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], // Substitution
                            Math.min(dp[i - 1][j], // Deletion
                                    dp[i][j - 1])); // Insertion
                }
            }
        }

        return dp[m][n];
    }
}
