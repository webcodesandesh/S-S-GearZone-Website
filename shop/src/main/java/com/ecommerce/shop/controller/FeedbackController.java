package com.ecommerce.shop.controller;

import com.ecommerce.shop.entity.Feedback;
import com.ecommerce.shop.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    // Admin endpoint to view all feedback
    @GetMapping
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllByOrderByIdDesc();
    }

    // User endpoint to submit feedback
    @PostMapping
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback) {
        if (feedback.getUserName() == null || feedback.getUserName().trim().isEmpty() || "null".equals(feedback.getUserName())) {
            feedback.setUserName("Player");
        }
        Feedback saved = feedbackRepository.save(feedback);
        return ResponseEntity.ok(saved);
    }

    // Admin endpoint to delete feedback
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        if (!feedbackRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Feedback not found");
        }
        feedbackRepository.deleteById(id);
        return ResponseEntity.ok("Feedback deleted successfully");
    }
}
