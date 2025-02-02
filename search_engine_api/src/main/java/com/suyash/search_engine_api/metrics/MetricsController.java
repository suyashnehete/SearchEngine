package com.suyash.search_engine_api.metrics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyash.search_engine_api.query.QueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/metrics")
public class MetricsController {

    private QueryService queryService;

    @GetMapping("/query-count")
    public int getQueryCount() {
        return queryService.getQueryCount();
    }

    @GetMapping("/cache-hit-rate")
    public double getCacheHitRate() {
        return queryService.getCacheHitRate();
    }
}
