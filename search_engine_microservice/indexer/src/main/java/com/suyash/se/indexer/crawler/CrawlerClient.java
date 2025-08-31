package com.suyash.se.indexer.crawler;

import java.util.Optional;

import com.suyash.se.indexer.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "crawler", configuration = FeignConfig.class)
public interface CrawlerClient {

    @PostMapping("/crawler/findByUrl")
    CrawledPage findByUrl(@RequestBody String url);

    @PostMapping("/crawler/save")
    Boolean save(@RequestBody CrawledPage page);

    @GetMapping("/crawler/findById/{id}")
    Optional<CrawledPage> findById(@PathVariable("id") long id);

    @GetMapping("/crawler/findAll")
    java.util.List<CrawledPage> findAllPages();

}
