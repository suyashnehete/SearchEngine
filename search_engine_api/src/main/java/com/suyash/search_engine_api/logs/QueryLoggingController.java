package com.suyash.search_engine_api.logs;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyash.search_engine_api.query.QueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("log")
public class QueryLoggingController {

    private final QueryService queryService;

    @PostMapping("log-query")
    public ResponseEntity<String> logQuery(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");
        String query = requestBody.get("query");

        if (userId == null || query == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        queryService.logUserQuery(userId, query);
        return ResponseEntity.ok("Query logged successfully");
    }
}
