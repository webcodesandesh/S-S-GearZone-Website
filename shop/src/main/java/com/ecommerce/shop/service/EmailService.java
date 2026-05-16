package com.ecommerce.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Sent when customer places an order
    public void sendOrderConfirmation(String toEmail, Long orderId, Double totalAmount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("✅ Order Confirmed — S.S GearZone #" + orderId);
        message.setText(
            "Hey Gamer! 🎮\n\n" +
            "Your order has been placed successfully at S.S GearZone!\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "  Order ID   : #" + orderId + "\n" +
            "  Amount     : ₹" + String.format("%.2f", totalAmount) + "\n" +
            "  Status     : PROCESSING\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "We are preparing your gear. You will receive another email\n" +
            "when your order is shipped.\n\n" +
            "Track your order anytime at: http://localhost:8080/orders.html\n\n" +
            "Thank you for shopping with S.S GearZone! ⚡\n" +
            "— The GearZone Team"
        );
        mailSender.send(message);
    }

    // ✅ Sent when admin marks order as SHIPPED or DELIVERED
    public void sendStatusUpdate(String toEmail, Long orderId, String status) {
        SimpleMailMessage message = new SimpleMailMessage();

        String emoji   = "SHIPPED".equals(status)   ? "🚚" : "📦";
        String subject = "SHIPPED".equals(status)
            ? emoji + " Your Order #" + orderId + " Has Been Shipped! — S.S GearZone"
            : emoji + " Your Order #" + orderId + " Has Been Delivered! — S.S GearZone";

        String body = "SHIPPED".equals(status)
            ? "Hey Gamer! 🎮\n\n" +
              "Great news! Your order is on its way! 🚚\n\n" +
              "━━━━━━━━━━━━━━━━━━━━━━━━\n" +
              "  Order ID : #" + orderId + "\n" +
              "  Status   : SHIPPED\n" +
              "━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
              "Your gear is heading to you. Stay ready!\n\n" +
              "Track your order: http://localhost:8080/orders.html\n\n" +
              "— S.S GearZone Team ⚡"
            : "Hey Gamer! 🎮\n\n" +
              "Your order has been DELIVERED! 📦\n\n" +
              "━━━━━━━━━━━━━━━━━━━━━━━━\n" +
              "  Order ID : #" + orderId + "\n" +
              "  Status   : DELIVERED\n" +
              "━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
              "Hope you love your new gear! Don't forget to leave a review.\n\n" +
              "Shop again: http://localhost:8080/products.html\n\n" +
              "— S.S GearZone Team ⚡";

        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}