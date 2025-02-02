package com.suyash.search_engine_api.query;

import lombok.Builder;

@Builder
public record UrlResponse(
    String url,
    String title,
    String shortContent
) {

}
