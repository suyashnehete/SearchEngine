package com.suyash.se.indexer.crawler;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrawledPage {

    private Long id;

    private String url;

    private String title;

    private String shortContent;

    private String content;

    private LocalDateTime createdDate;

    private List<String> tags;

    private double pageRankScore;
}

