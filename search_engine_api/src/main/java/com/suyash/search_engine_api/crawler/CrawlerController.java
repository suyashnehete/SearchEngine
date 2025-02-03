package com.suyash.search_engine_api.crawler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("crawler")
public class CrawlerController {

    private final WebCrawlerService webCrawlerService;

    @PostMapping
    public ResponseEntity<?> submitUrl(@RequestBody Map<String, String> requestBody) {
        String url = requestBody.get("url");
        if (url == null || !webCrawlerService.isValidUrl(url)) {
            return ResponseEntity.badRequest().body("Invalid URL");
        }

        webCrawlerService.addUrlToQueue(url);
        Map<String, String> response = new HashMap<>();
        response.put("message", "URL added to queue");
        response.put("url", url);
        return ResponseEntity.ok(response);
    }
}
