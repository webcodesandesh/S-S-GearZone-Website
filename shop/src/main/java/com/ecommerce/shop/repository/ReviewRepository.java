package com.ecommerce.shop.repository;
import com.ecommerce.shop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Kisi particular product ke reviews nikalne ke liye
    List<Review> findByProductIdOrderByReviewDateDesc(Long productId);
}