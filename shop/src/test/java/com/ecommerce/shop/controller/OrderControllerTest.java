package com.ecommerce.shop.controller;

import com.ecommerce.shop.entity.Order;
import com.ecommerce.shop.entity.Product;
import com.ecommerce.shop.repository.OrderRepository;
import com.ecommerce.shop.repository.ProductRepository;
import com.ecommerce.shop.service.EmailService;
import com.ecommerce.shop.service.SseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SseService sseService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCancelOrderAndRestoreStock() {
        // Arrange
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        mockOrder.setStatus("PROCESSING");
        mockOrder.setCartItems("[{\"id\": 10, \"quantity\": 2}]");

        Product mockProduct = new Product();
        mockProduct.setId(10L);
        mockProduct.setStockQuantity(5);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(productRepository.findById(10L)).thenReturn(Optional.of(mockProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        ResponseEntity<?> response = orderController.cancelOrder(orderId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("CANCELLED", mockOrder.getStatus());
        assertEquals(7, mockProduct.getStockQuantity()); // 5 + 2 restored
        verify(productRepository, times(1)).save(mockProduct);
        verify(orderRepository, times(1)).save(mockOrder);
    }
}
