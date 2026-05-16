package com.ecommerce.shop.repository;

import com.ecommerce.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Galti yahan thi, humne wapas isko theek kar diya hai:
    Optional<User> findByEmail(String email);

}