package com.suyash.se.crawler.crawler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final CrawledPageRepository crawledPageRepository;

    @PostMapping()
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

    @PostMapping("/save")
    public ResponseEntity<Boolean> save(@RequestBody CrawledPage page) {
        crawledPageRepository.save(page);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/findByUrl")
    public ResponseEntity<CrawledPage> findByUrl(@RequestBody String url) {
        return ResponseEntity.ok(crawledPageRepository.findByUrl(url));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<CrawledPage> findById(@PathVariable(name = "id") long key) {
        return ResponseEntity.ok(crawledPageRepository.findById(key).orElse(null));
    }
}
