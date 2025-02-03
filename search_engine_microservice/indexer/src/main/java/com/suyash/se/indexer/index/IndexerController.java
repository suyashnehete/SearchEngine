package com.suyash.se.indexer.index;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
