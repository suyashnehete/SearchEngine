package com.suyash.se.query.indexer;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvertedIndex {
    private Long id;

    private String word;

    private List<Integer> documentIds;

    private Map<Integer, Double> tfidfScores;
}