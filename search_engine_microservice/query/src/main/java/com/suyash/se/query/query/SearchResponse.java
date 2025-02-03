package com.suyash.se.query.query;

import java.util.List;

import lombok.Builder;

@Builder
public record SearchResponse(
        List<UrlResponse> documents,
        int totalResults,
        int totalPages,
        int currentPage,
        int pageSize
) {

}
