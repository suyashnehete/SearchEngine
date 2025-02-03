package com.suyash.se.crawler.indexer;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.suyash.se.crawler.crawler.CrawledPage;

@FeignClient(name = "indexer", url = "${application.config.indexer.url}")
public interface IndexerClient {

    @PostMapping
    Boolean buildIndex(@RequestBody List<CrawledPage> pages);
}
