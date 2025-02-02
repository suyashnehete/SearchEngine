package com.suyash.search_engine_api.feedback;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByQueryAndUserId(String query, String userId);
}
