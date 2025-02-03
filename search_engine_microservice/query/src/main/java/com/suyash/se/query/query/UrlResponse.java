package com.suyash.se.query.query;

import lombok.Builder;

@Builder
public record UrlResponse(
        long documentId,
        String url,
        String title,
        String shortContent
) {

}
