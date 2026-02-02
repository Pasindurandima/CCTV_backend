package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Inventory;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ReportController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    // Get comprehensive reports
    @GetMapping
    public ResponseEntity<Map<String, Object>> getReports(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            LocalDateTime start = startDate != null 
                ? LocalDate.parse(startDate).atStartOfDay() 
                : LocalDateTime.now().minusMonths(1);
            LocalDateTime end = endDate != null 
                ? LocalDate.parse(endDate).atTime(LocalTime.MAX) 
                : LocalDateTime.now();
            
            Map<String, Object> reports = new HashMap<>();
            reports.put("salesReport", getSalesReport(start, end));
            reports.put("inventoryReport", getInventoryReport());
            reports.put("productReport", getProductReport());
            
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    // Sales Report
    private List<Map<String, Object>> getSalesReport(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);
        
        return orders.stream().map(order -> {
            Map<String, Object> sale = new HashMap<>();
            sale.put("orderId", order.getId());
            sale.put("orderDate", order.getOrderDate());
            sale.put("customerName", order.getCustomerName());
            sale.put("productCount", order.getProductCount());
            sale.put("totalAmount", order.getTotalAmount());
            sale.put("status", order.getStatus());
            return sale;
        }).collect(Collectors.toList());
    }
    
    // Inventory Report
    private List<Map<String, Object>> getInventoryReport() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        List<Product> products = productRepository.findAll();
        
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        
        return inventoryList.stream().map(inv -> {
            Map<String, Object> report = new HashMap<>();
            Product product = productMap.get(inv.getProductId());
            
            report.put("productName", inv.getProductName());
            report.put("category", product != null ? product.getCategory() : "Unknown");
            report.put("quantity", inv.getQuantity());
            report.put("unitPrice", inv.getUnitPrice());
            report.put("stockValue", inv.getQuantity() * inv.getUnitPrice());
            report.put("isLowStock", inv.getQuantity() <= inv.getReorderLevel());
            return report;
        }).collect(Collectors.toList());
    }
    
    // Product Performance Report
    private List<Map<String, Object>> getProductReport() {
        List<Product> products = productRepository.findAll();
        List<Inventory> inventoryList = inventoryRepository.findAll();
        
        Map<Long, Inventory> inventoryMap = inventoryList.stream()
                .collect(Collectors.toMap(Inventory::getProductId, i -> i));
        
        Random random = new Random(); // For demo data
        
        return products.stream().map(product -> {
            Map<String, Object> report = new HashMap<>();
            Inventory inv = inventoryMap.get(product.getId());
            
            report.put("productName", product.getName());
            report.put("category", product.getCategory());
            report.put("unitsSold", random.nextInt(100)); // Simulated
            report.put("revenue", product.getPrice() * random.nextInt(100)); // Simulated
            report.put("avgRating", 4.0 + random.nextDouble()); // Simulated
            report.put("stockLeft", inv != null ? inv.getQuantity() : 0);
            return report;
        }).collect(Collectors.toList());
    }
    
    // Get sales summary
    @GetMapping("/sales-summary")
    public ResponseEntity<Map<String, Object>> getSalesSummary() {
        try {
            List<Order> allOrders = orderRepository.findAll();
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalOrders", allOrders.size());
            summary.put("totalRevenue", allOrders.stream()
                    .mapToDouble(Order::getTotalAmount)
                    .sum());
            summary.put("completedOrders", allOrders.stream()
                    .filter(o -> "COMPLETED".equals(o.getStatus()))
                    .count());
            summary.put("pendingOrders", allOrders.stream()
                    .filter(o -> "PENDING".equals(o.getStatus()))
                    .count());
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
