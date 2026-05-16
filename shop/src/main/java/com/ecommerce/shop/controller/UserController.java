package com.ecommerce.shop.controller;

import com.ecommerce.shop.entity.User;
import com.ecommerce.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // POST: Register a new user
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // POST: Login a user
    @PostMapping("/login")
    public User loginUser(@RequestBody User loginDetails) {
        Optional<User> user = userRepository.findByEmail(loginDetails.getEmail());

        // Check if user exists and password matches
        if (user.isPresent() && user.get().getPassword().equals(loginDetails.getPassword())) {
            return user.orElse(null);
        }
        return null;
    }

    // GET: Fetch all users (For Admin Dashboard)
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // PUT: Toggle Block/Unblock status (For Admin Dashboard)
    @PutMapping("/{id}/toggle-status")
    public User toggleUserStatus(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Prevent blocking an Admin account
            if (user.getRole().equals("ADMIN")) {
                throw new RuntimeException("Cannot block an Admin account.");
            }

            // Flip the status (true becomes false, false becomes true)
            user.setActive(!user.isActive());
            return userRepository.save(user);
        }
        return null;
    }

    // GET: Fetch user by email
    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // PUT: Update Profile Picture
    @PutMapping("/{email}/profile-pic")
    public User updateProfilePic(@PathVariable String email, @RequestBody java.util.Map<String, String> payload) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (payload.containsKey("profilePic")) {
                user.setProfilePic(payload.get("profilePic"));
                return userRepository.save(user);
            }
        }
        return null;
    }
}