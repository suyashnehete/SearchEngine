package com.suyash.se.indexer.crawler;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "crawler")
public interface CrawlerClient {

    @PostMapping("/findByUrl")
    CrawledPage findByUrl(@RequestBody String url);

    @PostMapping("/save")
    Boolean save(@RequestBody CrawledPage page);

    @GetMapping("/findById/{id}")
    Optional<CrawledPage> findById(@PathVariable(name = "id") long id);

}
