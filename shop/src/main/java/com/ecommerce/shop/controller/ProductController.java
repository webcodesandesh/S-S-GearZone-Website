package com.ecommerce.shop.controller;

import com.ecommerce.shop.entity.Product;
import com.ecommerce.shop.repository.ProductRepository;
import com.ecommerce.shop.service.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SseService sseService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 🔥 YAHI MISSING THA! (Single product laane ke liye) 🔥
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productRepository.findByCategory(category);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        Product saved = productRepository.save(product);
        sseService.broadcast("product-added",
                "{\"id\":\"" + saved.getId() + "\",\"name\":\"" + saved.getName() + "\"}");
        return saved;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setStockQuantity(productDetails.getStockQuantity());
            product.setImages(productDetails.getImages());
            product.setCategory(productDetails.getCategory());
            product.setSubCategory(productDetails.getSubCategory());
            product.setDiscountPercent(productDetails.getDiscountPercent());
            Product updated = productRepository.save(product);
            sseService.broadcast("product-updated",
                    "{\"id\":\"" + updated.getId() + "\",\"name\":\"" + updated.getName() + "\"}");
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        sseService.broadcast("product-deleted", "{\"id\":\"" + id + "\"}");
        return ResponseEntity.ok().build();
    }
}