package com.example.orderservice2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.orderservice2.DTO.ProductResponse;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    ProductResponse getProductById(@PathVariable String id);

    @PutMapping("/products/{id}/reduceStock")
    ResponseEntity<String> reduceStock(@PathVariable String id, @RequestParam int qty);
}
