package com.suyash.search_engine_api.crawler;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawler")
public class CrawlerController {

    private WebCrawlerService webCrawlerService;

    @PostMapping("/submit-url")
    public ResponseEntity<String> submitUrl(@RequestBody Map<String, String> requestBody) {
        String url = requestBody.get("url");
        if (url == null || !webCrawlerService.isValidUrl(url)) {
            return ResponseEntity.badRequest().body("Invalid URL");
        }

        webCrawlerService.addUrlToQueue(url);
        return ResponseEntity.ok("URL added to queue: " + url);
    }
}
