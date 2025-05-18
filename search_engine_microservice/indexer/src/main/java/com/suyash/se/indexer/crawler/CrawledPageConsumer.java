package com.suyash.se.indexer.crawler;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.suyash.se.indexer.index.IndexerService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CrawledPageConsumer {

    private final IndexerService indexerService;

    @KafkaListener(topics = "crawled_pages")
    public void processCrawledPage(List<CrawledPage> pages) {
        for(CrawledPage page : pages){
            System.out.println("Received crawled page: " + page.getUrl());
        }
        indexerService.buildIndex(pages);
    }
}
