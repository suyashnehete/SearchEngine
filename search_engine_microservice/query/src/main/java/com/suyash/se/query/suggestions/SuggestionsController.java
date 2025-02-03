package com.suyash.se.query.suggestions;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suyash.se.query.query.QueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("suggestions")
public class SuggestionsController {
    private final QueryService queryService;

    @GetMapping("trie")
    public List<String> getSuggestionsTrie(@RequestParam String prefix) {
        return queryService.getSuggestionsTrie(prefix);
    }

    @GetMapping("ngram")
    public List<String> getSuggestionsNGram(@RequestParam String prefix) {
        return queryService.getSuggestionsNGram(prefix);
    }

    @GetMapping
    public List<String> getSuggestions(@RequestParam String userId, @RequestParam String prefix) {
        return queryService.getContextAwareSuggestions(userId, prefix);
    }
}
