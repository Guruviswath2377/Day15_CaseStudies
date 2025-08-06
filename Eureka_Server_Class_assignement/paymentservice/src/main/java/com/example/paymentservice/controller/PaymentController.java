package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @PostMapping
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());

        String status = request.getAmount() > 0 ? "SUCCESS" : "FAILED";
        payment.setStatus(status);

        paymentRepository.save(payment);

        PaymentResponse response = new PaymentResponse();
        response.setStatus(status);
        return response;
    }
}
