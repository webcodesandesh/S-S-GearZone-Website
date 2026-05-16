package com.ecommerce.shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- Make sure this is imported!
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    @JsonIgnore // <-- THIS IS THE MAGIC FIX! It stops the infinite loop.
    private List<Category> subCategories;
}