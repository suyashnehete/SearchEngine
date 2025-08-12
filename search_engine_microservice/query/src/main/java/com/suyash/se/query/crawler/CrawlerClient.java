package com.suyash.se.query.crawler;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "crawler")
public interface CrawlerClient {

    @GetMapping("/findById/{id}")
    Optional<CrawledPage> findById(@PathVariable("id") long id);

}
