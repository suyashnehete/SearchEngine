package com.suyash.se.indexer.index;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvertedIndexRepository extends JpaRepository<InvertedIndex, Long> {
    InvertedIndex findByWord(String word);
}
