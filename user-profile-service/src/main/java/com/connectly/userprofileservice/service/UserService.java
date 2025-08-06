package com.connectly.userprofileservice.service;
import com.connectly.userprofileservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.connectly.userprofileservice.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Cacheable(value = "users", key = "#userId")
    public User getUserFromCache(String userId) {
        return (User) userRepository.findById(userId).orElse(null);
    }
    @CircuitBreaker(name = "userServiceCircuitBreaker", fallbackMethod = "getUserFromFallback")
    public User getUser(String userId) {
        return (User) userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User getUserFromFallback(String userId, Throwable t) {
        System.out.println("Fallback triggered for user: " + userId);
        return getUserFromCache(userId); // returns cache or null
    }
}