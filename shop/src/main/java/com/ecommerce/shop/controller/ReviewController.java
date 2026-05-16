package com.ecommerce.shop.controller;

import com.ecommerce.shop.entity.Product;
import com.ecommerce.shop.entity.Review;
import com.ecommerce.shop.repository.ProductRepository;
import com.ecommerce.shop.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByReviewDateDesc(productId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addReview(@PathVariable Long productId, @RequestBody Review reviewRequest) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if(!productOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Product not found");
        }
        
        Review review = new Review();
        review.setProduct(productOpt.get());
        review.setReviewerName(reviewRequest.getReviewerName());
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        
        Review savedReview = reviewRepository.save(review);
        return ResponseEntity.ok(savedReview);
    }
}
