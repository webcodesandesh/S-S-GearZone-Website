package com.ecommerce.shop.repository;

import com.ecommerce.shop.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // User ke saare favourite items laane ke liye
    List<Wishlist> findByUserEmail(String userEmail);

    // Check karne ke liye ki item already wishlist me hai ya nahi
    Wishlist findByUserEmailAndProductId(String userEmail, Long productId);
}