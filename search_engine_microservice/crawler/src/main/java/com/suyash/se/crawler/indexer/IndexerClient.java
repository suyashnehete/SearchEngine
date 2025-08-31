package com.suyash.se.crawler.indexer;

import java.util.List;

import com.suyash.se.crawler.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.suyash.se.crawler.crawler.CrawledPage;

@FeignClient(name = "indexer", configuration = FeignConfig.class)
public interface IndexerClient {

    @PostMapping("/indexer")
    Boolean buildIndex(@RequestBody List<CrawledPage> pages);
}
