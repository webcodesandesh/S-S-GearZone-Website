package com.ecommerce.shop.repository;

import com.ecommerce.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Naya sahi method: Date ki jagah ID se sort karega (Error Free)
    List<Order> findByUserEmailOrderByIdDesc(String userEmail);

}