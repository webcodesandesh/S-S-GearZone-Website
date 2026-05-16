package com.ecommerce.shop.controller;

import com.ecommerce.shop.entity.User;
import com.ecommerce.shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        User loginDetails = new User();
        loginDetails.setEmail("test@example.com");
        loginDetails.setPassword("password123");

        // Act
        User result = userController.loginUser(loginDetails);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testLoginFailureInvalidPassword() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("correctPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        User loginDetails = new User();
        loginDetails.setEmail("test@example.com");
        loginDetails.setPassword("wrongPassword");

        // Act
        User result = userController.loginUser(loginDetails);

        // Assert
        assertNull(result);
    }

    @Test
    void testLoginFailureUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        User loginDetails = new User();
        loginDetails.setEmail("nonexistent@example.com");
        loginDetails.setPassword("anyPassword");

        // Act
        User result = userController.loginUser(loginDetails);

        // Assert
        assertNull(result);
    }
}
