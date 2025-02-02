package com.suyash.search_engine_api.query;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class QueryController {

    private final QueryService queryService;

    @GetMapping("/search")
    public SearchResponse search(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int topK,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return queryService.processQueryWithCorrections(query, topK, page, size);
    }
}
