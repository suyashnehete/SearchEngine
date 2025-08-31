package com.suyash.se.query.crawler;

import java.util.Optional;

import com.suyash.se.query.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "crawler", configuration = FeignConfig.class)
public interface CrawlerClient {

    @GetMapping("/crawler/findById/{id}")
    Optional<CrawledPage> findById(@PathVariable("id") long id);

}
