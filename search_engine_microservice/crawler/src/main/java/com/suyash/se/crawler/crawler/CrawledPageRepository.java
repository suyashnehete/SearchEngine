package com.suyash.se.crawler.crawler;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawledPageRepository extends JpaRepository<CrawledPage, Long> {
    boolean existsByUrl(String url);

    CrawledPage findByUrl(String nextUrl);
}
