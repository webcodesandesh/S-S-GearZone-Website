package com.ecommerce.shop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false)
    private double totalAmount;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String cartItems;

    // NEW FIELD: Tracks the current status of the order
    @Column(nullable = false)
    private String status = "PROCESSING";

    // NEW FIELDS: Timestamp tracking
    @Column
    private String orderDate;

    @Column
    private String shippedDate;

    @Column
    private String deliveredDate;

    @Column
    private String canceledDate;

    // --- GETTERS AND SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getCartItems() { return cartItems; }
    public void setCartItems(String cartItems) { this.cartItems = cartItems; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getShippedDate() { return shippedDate; }
    public void setShippedDate(String shippedDate) { this.shippedDate = shippedDate; }

    public String getDeliveredDate() { return deliveredDate; }
    public void setDeliveredDate(String deliveredDate) { this.deliveredDate = deliveredDate; }

    public String getCanceledDate() { return canceledDate; }
    public void setCanceledDate(String canceledDate) { this.canceledDate = canceledDate; }
}