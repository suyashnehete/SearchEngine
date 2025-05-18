package com.suyash.se.query.query.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    public List<String> getSuggestions(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return Collections.emptyList();
            }
        }
        return collectWords(node, prefix);
    }

    private List<String> collectWords(TrieNode node, String prefix) {
        List<String> words = new ArrayList<>();
        if (node.isEndOfWord) {
            words.add(prefix);
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            words.addAll(collectWords(entry.getValue(), prefix + entry.getKey()));
        }
        return words;
    }
}

