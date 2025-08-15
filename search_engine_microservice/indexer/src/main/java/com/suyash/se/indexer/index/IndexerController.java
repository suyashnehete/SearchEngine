package com.suyash.se.indexer.index;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.suyash.se.indexer.crawler.CrawledPage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("indexer")
@RequiredArgsConstructor
public class IndexerController {

    private final IndexerService indexerService;
    private final InvertedIndexRepository invertedIndexRepository;

    @PostMapping
    public ResponseEntity<Boolean> buildIndex(@RequestBody List<CrawledPage> pages) {
        indexerService.buildIndex(pages);
        return ResponseEntity.ok(true);
    }

    @PostMapping("find")
    public ResponseEntity<InvertedIndex> findByWord(@RequestBody String term) {
        return ResponseEntity.ok(invertedIndexRepository.findByWord(term));
    }

    // Admin endpoints
    @PostMapping("admin/reindex")
    public ResponseEntity<String> reindexAll() {
        indexerService.reindexAll();
        return ResponseEntity.ok("Reindexing started");
    }

    @PostMapping("admin/optimize")
    public ResponseEntity<String> optimizeIndex() {
        // Optimize index for better performance
        indexerService.optimizeIndex();
        return ResponseEntity.ok("Index optimization started");
    }

    @DeleteMapping("admin/index")
    public ResponseEntity<String> clearIndex() {
        // Clear entire index - dangerous operation
        indexerService.clearIndex();
        return ResponseEntity.ok("Index cleared");
    }

    @GetMapping("admin/stats")
    public ResponseEntity<Map<String, Object>> getIndexStats() {
        // Return index statistics
        Map<String, Object> stats = indexerService.getIndexStatistics();
        return ResponseEntity.ok(stats);
    }

}
