package com.suyash.se.query.query;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResponse{

        List<UrlResponse> documents;
        int totalResults;
        int totalPages;
        int currentPage;
        int pageSize;
}
