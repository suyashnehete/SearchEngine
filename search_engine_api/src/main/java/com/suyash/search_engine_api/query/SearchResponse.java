package com.suyash.search_engine_api.query;

import java.util.List;

import lombok.Builder;

@Builder
public record SearchResponse(
        List<Integer> documentIds,
        int totalResults,
        int totalPages,
        int currentPage,
        int pageSize
) {

}
