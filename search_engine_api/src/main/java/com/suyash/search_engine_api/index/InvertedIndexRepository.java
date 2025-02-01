package com.suyash.search_engine_api.index;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvertedIndexRepository extends JpaRepository<InvertedIndex, Long> {
    InvertedIndex findByWord(String word);
}
