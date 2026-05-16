package com.ecommerce.shop.service;

import com.ecommerce.shop.entity.Product;
import com.ecommerce.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Product getProductById(Long id) {
        // findById is built into Spring Data JPA!
        return productRepository.findById(id).orElse(null);
    }
}