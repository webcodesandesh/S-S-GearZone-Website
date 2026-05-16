package com.ecommerce.shop.controller;

import com.ecommerce.shop.entity.Product;
import com.ecommerce.shop.entity.Wishlist;
import com.ecommerce.shop.repository.ProductRepository;
import com.ecommerce.shop.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    // GET user's wishlist
    @GetMapping("/{email}")
    public List<Wishlist> getUserWishlist(@PathVariable String email) {
        return wishlistRepository.findByUserEmail(email);
    }

    // ADD or REMOVE from wishlist (Toggle logic)
    @PostMapping("/toggle")
    public String toggleWishlist(@RequestParam String email, @RequestParam Long productId) {
        Wishlist existing = wishlistRepository.findByUserEmailAndProductId(email, productId);

        if (existing != null) {
            // Agar already hai, toh Remove kar do (Unlike)
            wishlistRepository.delete(existing);
            return "Removed";
        } else {
            // Agar nahi hai, toh Add kar do (Like)
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                Wishlist wishlist = new Wishlist();
                wishlist.setUserEmail(email);
                wishlist.setProduct(product);
                wishlistRepository.save(wishlist);
                return "Added";
            }
            return "Error: Product not found";
        }
    }
}