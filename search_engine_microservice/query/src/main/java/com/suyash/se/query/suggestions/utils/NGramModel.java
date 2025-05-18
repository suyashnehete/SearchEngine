package com.suyash.se.query.suggestions.utils;

import java.util.*;

import org.springframework.stereotype.Component;

public class NGramModel {
    private final Map<String, Map<String, Integer>> nGramMap = new HashMap<>();
    private final int n;

    public NGramModel(int n) {
        this.n = n;
    }

    public void train(List<String> queries) {
        for (String query : queries) {
            List<String> tokens = tokenize(query);
            for (int i = 0; i <= tokens.size() - n; i++) {
                String prefix = String.join(" ", tokens.subList(i, i + n - 1));
                String nextWord = tokens.get(i + n - 1);

                nGramMap.computeIfAbsent(prefix, k -> new HashMap<>()).merge(nextWord, 1, Integer::sum);
            }
        }
    }

    public List<String> getSuggestions(String prefix) {
        Map<String, Integer> suggestions = nGramMap.getOrDefault(prefix, new HashMap<>());
        return suggestions.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<String> tokenize(String text) {
        return Arrays.asList(text.toLowerCase().split("\\s+"));
    }
}