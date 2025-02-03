package com.suyash.se.query.indexer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "indexer", url = "${application.config.indexer.url}")
public interface IndexerClient {

    @PostMapping("find")
    InvertedIndex findByWord(@RequestBody String term);

    
}
