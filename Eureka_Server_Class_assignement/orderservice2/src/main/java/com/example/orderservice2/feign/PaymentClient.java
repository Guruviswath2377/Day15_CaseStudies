package com.example.orderservice2.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.orderservice2.DTO.PaymentRequest;
import com.example.orderservice2.DTO.PaymentResponse;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    @PostMapping("/payments")
    PaymentResponse processPayment(@RequestBody PaymentRequest paymentRequest);
}