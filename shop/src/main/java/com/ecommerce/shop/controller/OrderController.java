package com.ecommerce.shop.controller;

import com.ecommerce.shop.entity.Order;
import com.ecommerce.shop.entity.Product;
import com.ecommerce.shop.repository.OrderRepository;
import com.ecommerce.shop.repository.ProductRepository;
import com.ecommerce.shop.service.EmailService;
import com.ecommerce.shop.service.SseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // 1. ADDED PRODUCT REPOSITORY FOR STOCK MANAGEMENT
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SseService sseService;

    @Autowired
    private EmailService emailService;

    // GET ALL ORDERS
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 2. CREATE NEW ORDER (NOW MINUSES STOCK)
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            System.out.println("--- NEW ORDER RECEIVED ---");
            System.out.println("Cart Items JSON: " + order.getCartItems());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode cartItemsNode = mapper.readTree(order.getCartItems());

            for (JsonNode item : cartItemsNode) {
                Long productId = item.get("id").asLong();
                int quantity = item.get("quantity").asInt();

                System.out.println("Looking for Product ID: " + productId + " | Qty to minus: " + quantity);

                Product product = productRepository.findById(productId).orElse(null);

                if (product != null) {
                    int oldStock = product.getStockQuantity();
                    int newStock = oldStock - quantity;
                    product.setStockQuantity(Math.max(newStock, 0));
                    productRepository.save(product);

                    System.out.println("SUCCESS: " + product.getName() + " Stock updated! Old: " + oldStock + " -> New: " + newStock);
                } else {
                    System.out.println("FAILED: Product with ID " + productId + " NOT FOUND in Database!");
                }
            }

            order.setStatus("PROCESSING");
            order.setOrderDate(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")));
            Order savedOrder = orderRepository.save(order);
            System.out.println("Order saved successfully with ID: " + savedOrder.getId());

            // ✅ Send confirmation email to customer
            try {
                emailService.sendOrderConfirmation(
                    savedOrder.getUserEmail(),
                    savedOrder.getId(),
                    savedOrder.getTotalAmount()
                );
                System.out.println("✅ Confirmation email sent to: " + savedOrder.getUserEmail());
            } catch (Exception mailEx) {
                // Don't fail the order if email fails
                System.out.println("⚠️ Email failed (order still saved): " + mailEx.getMessage());
            }

            // Notify admin panel about new order
            sseService.broadcast("order-new",
                "{\"id\":\"" + savedOrder.getId() + "\",\"email\":\"" + savedOrder.getUserEmail() + "\"}");
            return ResponseEntity.ok(savedOrder);

        } catch (Exception e) {
            System.out.println("CRITICAL ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to process order: " + e.getMessage());
        }
    }

    // 3. USER CANCELS AN ORDER (NOW RESTORES/PLUSES STOCK)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            Order order = orderRepository.findById(id).orElse(null);

            if (order != null) {
                // User can only cancel if it is still processing
                if (order.getStatus().equals("PROCESSING")) {
                    order.setStatus("CANCELLED");
                    order.setCanceledDate(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")));

                    // --- RESTORE STOCK LOGIC ---
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode cartItemsNode = mapper.readTree(order.getCartItems());

                    for (JsonNode item : cartItemsNode) {
                        Long productId = item.get("id").asLong();
                        int quantity = item.get("quantity").asInt();

                        Product product = productRepository.findById(productId).orElse(null);
                        if (product != null) {
                            // Plus the stock back to the database
                            product.setStockQuantity(product.getStockQuantity() + quantity);
                            productRepository.save(product);
                        }
                    }
                    // ---------------------------

                    Order updatedOrder = orderRepository.save(order);
                    return ResponseEntity.ok(updatedOrder);
                } else {
                    return ResponseEntity.badRequest().body("Order has already been shipped or delivered.");
                }
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error cancelling order: " + e.getMessage());
        }
    }

    // ADMIN UPDATES ORDER STATUS (For Admin Panel use)
    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            String newStatus = status.toUpperCase();
            order.setStatus(newStatus);
            String now = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
            if ("SHIPPED".equals(newStatus)) {
                order.setShippedDate(now);
            } else if ("DELIVERED".equals(newStatus)) {
                order.setDeliveredDate(now);
            } else if ("CANCELLED".equals(newStatus) || "CANCELED".equals(newStatus)) {
                order.setCanceledDate(now);
            }
            
            Order updated = orderRepository.save(order);

            // ✅ Send status update email to customer
            if ("SHIPPED".equals(newStatus) || "DELIVERED".equals(newStatus)) {
                try {
                    emailService.sendStatusUpdate(
                        updated.getUserEmail(),
                        updated.getId(),
                        newStatus
                    );
                    System.out.println("✅ Status email sent to: " + updated.getUserEmail() + " — " + newStatus);
                } catch (Exception mailEx) {
                    System.out.println("⚠️ Status email failed: " + mailEx.getMessage());
                }
            }

            // Notify the user's browser about their order status change
            sseService.broadcast("order-status-changed",
                "{\"id\":\"" + updated.getId() + "\",\"email\":\"" + updated.getUserEmail() +
                "\",\"status\":\"" + updated.getStatus() + "\"}");
            return updated;
        }
        return null;
    }
}