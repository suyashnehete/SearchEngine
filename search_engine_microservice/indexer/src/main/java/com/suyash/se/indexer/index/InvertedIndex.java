package com.suyash.se.indexer.index;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Type;

import com.suyash.se.indexer.converter.JsonbMapConverter;
import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Entity(name = "inverted_index")
public class InvertedIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "document_ids", joinColumns = @JoinColumn(name = "inverted_index_id"))
    private List<Integer> documentIds;

    @Type(JsonType.class)
    @Column(columnDefinition = "JSONB")
    @Convert(converter = JsonbMapConverter.class)
    private Map<Integer, Double> tfidfScores;
}