package com.example.productservice1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.productservice1.entity.Product;
import com.example.productservice1.repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository repo;

    @PostMapping
    public Product addProduct(@RequestBody Product p) {
        return repo.save(p);
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable String id) {
        return repo.findById(id).orElse(null);
    }

    @GetMapping
    public List<Product> getAll() {
        return repo.findAll();
    }

    @PutMapping("/{id}/reduceStock")
    public ResponseEntity<String> reduceStock(@PathVariable String id, @RequestParam int qty) {
        Product product = repo.findById(id).orElse(null);
        if (product != null && product.getStock() >= qty) {
            product.setStock(product.getStock() - qty);
            repo.save(product);
            return ResponseEntity.ok("Stock reduced.");
        }
        return ResponseEntity.badRequest().body("Insufficient stock.");
    }
}
