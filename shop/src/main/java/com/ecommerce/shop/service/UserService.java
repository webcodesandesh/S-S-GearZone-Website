package com.ecommerce.shop.service;

import com.ecommerce.shop.entity.User;
import com.ecommerce.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Method to register a new user
    public User registerUser(User user) {
        // Set default role for new signups
        user.setRole("USER");
        return userRepository.save(user);
    }

    // Method to find a user by email (useful for login later)
    // YAHAN THI GALTI: Optional hata diya aur sirf User rakha
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}