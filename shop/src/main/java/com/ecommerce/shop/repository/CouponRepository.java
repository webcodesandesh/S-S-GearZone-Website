package com.ecommerce.shop.repository;

import com.ecommerce.shop.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // Custom method to find a coupon by its code (e.g., "DIWALI20")
    Coupon findByCode(String code);
}