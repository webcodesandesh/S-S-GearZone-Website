package com.ecommerce.shop.controller;

import com.ecommerce.shop.entity.Coupon;
import com.ecommerce.shop.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*") // Cross-Origin allow karne ke liye
public class CouponController {

    @Autowired
    private CouponRepository couponRepository;

    // 1. GET ALL COUPONS (Admin Panel ke table ke liye)
    @GetMapping
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    // 2. CREATE NEW COUPON (Naya promo code banane ke liye)
    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        // Check karna ki code pehle se toh nahi hai
        Coupon existing = couponRepository.findByCode(coupon.getCode().toUpperCase());
        if (existing != null) {
            return ResponseEntity.badRequest().body("{\"message\": \"Coupon code already exists!\"}");
        }

        coupon.setCode(coupon.getCode().toUpperCase()); // Hamesha uppercase me save hoga
        coupon.setActive(true); // Default status Active rahega
        Coupon savedCoupon = couponRepository.save(coupon);

        return ResponseEntity.ok(savedCoupon);
    }

    // 3. UPDATE EXISTING COUPON (Admin Panel me Edit karne ke liye)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody Coupon updatedCoupon) {
        Coupon existing = couponRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setCode(updatedCoupon.getCode().toUpperCase());
            existing.setDiscountPercent(updatedCoupon.getDiscountPercent());

            // Status wahi rahega jo pehle tha
            return ResponseEntity.ok(couponRepository.save(existing));
        }
        return ResponseEntity.notFound().build();
    }

    // 4. DELETE A COUPON (Admin Panel se permanently udane ke liye)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        if (couponRepository.existsById(id)) {
            couponRepository.deleteById(id);
            return ResponseEntity.ok().body("{\"message\": \"Coupon deleted successfully\"}");
        }
        return ResponseEntity.notFound().build();
    }

    // 5. TOGGLE COUPON STATUS (Enable/Disable karne ke liye)
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleCouponStatus(@PathVariable Long id) {
        Coupon existing = couponRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setActive(!existing.isActive()); // True ko False, False ko True karega
            return ResponseEntity.ok(couponRepository.save(existing));
        }
        return ResponseEntity.notFound().build();
    }

    // 6. APPLY COUPON (Cart me discount check karne ke liye)
    @GetMapping("/apply")
    public ResponseEntity<?> applyCoupon(@RequestParam String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase());

        if (coupon != null) {
            if (coupon.isActive()) {
                // Agar code sahi hai aur active hai toh discount bhej do
                return ResponseEntity.ok(coupon);
            } else {
                // Code expire ya disable ho gaya hai
                return ResponseEntity.badRequest().body("{\"message\": \"Coupon is disabled or expired.\"}");
            }
        }
        // Code galat hai
        return ResponseEntity.badRequest().body("{\"message\": \"Invalid coupon code.\"}");
    }
}