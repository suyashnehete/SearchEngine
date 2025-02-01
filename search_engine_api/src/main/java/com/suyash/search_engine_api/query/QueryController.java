package com.suyash.search_engine_api.query;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class QueryController {
    
    private final QueryService queryService;

    @GetMapping("/search")
    public List<Integer> search(@RequestParam String query, @RequestParam(defaultValue = "10") int topK) {
        return queryService.processQuery(query, topK);
    }
}
