package com.suyash.se.query.indexer;

import com.suyash.se.query.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "indexer", configuration = FeignConfig.class)
public interface IndexerClient {

    @PostMapping("/indexer/find")
    InvertedIndex findByWord(@RequestBody String term);

}
