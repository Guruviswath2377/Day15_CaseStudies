package com.example.orderservice2.controller;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.orderservice2.DTO.OrderRequest;
import com.example.orderservice2.DTO.PaymentRequest;
import com.example.orderservice2.DTO.PaymentResponse;
import com.example.orderservice2.DTO.ProductResponse;
import com.example.orderservice2.entity.Order;
import com.example.orderservice2.feign.PaymentClient;
import com.example.orderservice2.feign.ProductClient;
import com.example.orderservice2.repository.OrderRepository;
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private ProductClient productClient;
    @Autowired
    private PaymentClient paymentClient;
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        ProductResponse product = productClient.getProductById(request.getProductId());
        if (product == null || product.getStock() < request.getQuantity()) {
            return ResponseEntity.badRequest().body("Product unavailable or insufficient stock.");
        }
        double total = product.getPrice() * request.getQuantity();
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setId(orderId);
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(total);
        order.setStatus("PENDING_PAYMENT");
        orderRepo.save(order);
        PaymentRequest paymentReq = new PaymentRequest();
        paymentReq.setOrderId(orderId);
        paymentReq.setAmount(total);
        paymentReq.setPaymentMethod(request.getPaymentMethod());
        PaymentResponse paymentResp = paymentClient.processPayment(paymentReq);
        if ("SUCCESS".equalsIgnoreCase(paymentResp.getStatus())) {
            order.setStatus("CONFIRMED");
            productClient.reduceStock(product.getId(), request.getQuantity());
        } else {
            order.setStatus("FAILED");
        }
        orderRepo.save(order);
        return ResponseEntity.ok(order);
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        Optional<Order> order = orderRepo.findById(orderId);
        return order.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(
            @PathVariable String orderId,
            @RequestBody OrderRequest request) {

        Optional<Order> existingOrderOpt = orderRepo.findById(orderId);
        if (existingOrderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Order order = existingOrderOpt.get();
        ProductResponse product = productClient.getProductById(request.getProductId());
        if (product == null || product.getStock() < request.getQuantity()) {
            return ResponseEntity.badRequest().body("Product unavailable or insufficient stock for update.");
        }
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(product.getPrice() * request.getQuantity());
        order.setStatus("UPDATED");
        orderRepo.save(order);
       return ResponseEntity.ok(order);
    }
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable String orderId) {
        Optional<Order> order = orderRepo.findById(orderId);
        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        orderRepo.deleteById(orderId);
        return ResponseEntity.ok("Order with ID " + orderId + " has been cancelled and deleted.");
    }
}