package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.SalesHistory;
import com.example.demo.repository.SalesHistoryRepository;

@RestController
@RequestMapping("/api/sales-history")
@CrossOrigin(origins = "http://localhost:5173")
public class SalesHistoryController {
    
    @Autowired
    private SalesHistoryRepository salesHistoryRepository;
    
    // Get all sales history
    @GetMapping
    public ResponseEntity<List<SalesHistory>> getAllSalesHistory() {
        List<SalesHistory> salesHistory = salesHistoryRepository.findAllByOrderByCompletedDateDesc();
        return ResponseEntity.ok(salesHistory);
    }
    
    // Get sales history by ID
    @GetMapping("/{id}")
    public ResponseEntity<SalesHistory> getSalesHistoryById(@PathVariable Long id) {
        return salesHistoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Search sales history
    @GetMapping("/search")
    public ResponseEntity<List<SalesHistory>> searchSalesHistory(@RequestParam String query) {
        List<SalesHistory> results = salesHistoryRepository.searchSalesHistory(query);
        return ResponseEntity.ok(results);
    }
    
    // Get sales history by customer email
    @GetMapping("/customer/email/{email}")
    public ResponseEntity<List<SalesHistory>> getSalesHistoryByEmail(@PathVariable String email) {
        List<SalesHistory> salesHistory = salesHistoryRepository.findByCustomerEmail(email);
        return ResponseEntity.ok(salesHistory);
    }
    
    // Get sales history by customer phone
    @GetMapping("/customer/phone/{phone}")
    public ResponseEntity<List<SalesHistory>> getSalesHistoryByPhone(@PathVariable String phone) {
        List<SalesHistory> salesHistory = salesHistoryRepository.findByCustomerPhone(phone);
        return ResponseEntity.ok(salesHistory);
    }
    
    // Get sales history by order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<SalesHistory> getSalesHistoryByOrderId(@PathVariable Long orderId) {
        SalesHistory salesHistory = salesHistoryRepository.findByOrderId(orderId);
        if (salesHistory != null) {
            return ResponseEntity.ok(salesHistory);
        }
        return ResponseEntity.notFound().build();
    }
}
