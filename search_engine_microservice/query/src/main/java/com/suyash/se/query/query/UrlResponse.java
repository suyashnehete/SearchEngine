package com.suyash.se.query.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlResponse{

        long documentId;
        String url;
        String title;
        String shortContent;

}
