package com.suyash.search_engine_api.query;

import lombok.Builder;

@Builder
public record UrlResponse(
        long documentId,
        String url,
        String title,
        String shortContent
) {

}
