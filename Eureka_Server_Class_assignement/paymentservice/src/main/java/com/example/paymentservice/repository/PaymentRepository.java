package com.example.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.paymentservice.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
}
