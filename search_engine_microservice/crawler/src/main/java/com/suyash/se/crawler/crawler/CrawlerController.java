package com.suyash.se.crawler.crawler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyash.se.crawler.dto.UrlRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("crawler")
@Validated
public class CrawlerController {

    private final WebCrawlerService webCrawlerService;
    private final CrawledPageRepository crawledPageRepository;

    @PostMapping()
    public ResponseEntity<?> submitUrl(@Valid @RequestBody UrlRequest request) {
        try {
            webCrawlerService.addUrlToQueue(request.getUrl());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "URL added to queue successfully");
            response.put("url", request.getUrl());
            response.put("priority", request.getPriority());
            response.put("maxDepth", request.getMaxDepth());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to add URL to queue");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Boolean> save(@RequestBody CrawledPage page) {
        crawledPageRepository.save(page);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/findByUrl")
    public ResponseEntity<CrawledPage> findByUrl(@Valid @RequestBody UrlRequest request) {
        try {
            CrawledPage page = crawledPageRepository.findByUrl(request.getUrl());
            if (page == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<CrawledPage> findById(@PathVariable("id") @Min(1) long id) {
        try {
            return crawledPageRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<java.util.List<CrawledPage>> findAllPages() {
        try {
            java.util.List<CrawledPage> pages = crawledPageRepository.findAll();
            return ResponseEntity.ok(pages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Admin endpoints
    @PostMapping("/admin/start")
    public ResponseEntity<String> startCrawler() {
        // Start crawler service
        webCrawlerService.startCrawling();
        return ResponseEntity.ok("Crawler service started");
    }

    @PostMapping("/admin/stop")
    public ResponseEntity<String> stopCrawler() {
        // Stop crawler service
        webCrawlerService.stopCrawling();
        return ResponseEntity.ok("Crawler service stopped");
    }

    @GetMapping("/admin/queue")
    public ResponseEntity<Map<String, Object>> getCrawlerQueue() {
        // Return crawler queue status
        Map<String, Object> queueInfo = webCrawlerService.getQueueStatus();
        return ResponseEntity.ok(queueInfo);
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> getCrawlerStats() {
        // Return crawler statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPagesCrawled", crawledPageRepository.count());
        stats.put("queueSize", webCrawlerService.getQueueSize());
        stats.put("isRunning", webCrawlerService.isRunning());
        return ResponseEntity.ok(stats);
    }
}
