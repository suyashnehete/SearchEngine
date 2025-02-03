package com.suyash.se.query.logs;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyash.se.query.query.QueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("log")
public class QueryLoggingController {

    private final QueryService queryService;

    @PostMapping("log-query")
    public ResponseEntity<?> logQuery(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");
        String query = requestBody.get("query");

        if (userId == null || query == null) {
            return ResponseEntity.badRequest().build();
        }

        queryService.logUserQuery(userId, query);
        return ResponseEntity.ok().build();
    }
}
