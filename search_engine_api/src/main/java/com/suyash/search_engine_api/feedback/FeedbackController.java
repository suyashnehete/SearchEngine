package com.suyash.search_engine_api.feedback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("feedback")
public class FeedbackController {
    
    private FeedbackRepository feedbackRepository;

    @PostMapping
    public ResponseEntity<String> logFeedback(@RequestBody Feedback feedback) {
        feedbackRepository.save(feedback);
        return ResponseEntity.ok("Feedback logged successfully");
    }
}
