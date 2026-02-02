package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.SalesHistory;
import com.example.demo.repository.SalesHistoryRepository;

@RestController
@RequestMapping("/api/profit-analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class ProfitAnalyticsController {
    
    @Autowired
    private SalesHistoryRepository salesHistoryRepository;
    
    // Get all profit analytics (total, daily, monthly, category-wise)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProfitAnalytics() {
        try {
            List<SalesHistory> allSales = salesHistoryRepository.findAllByOrderByCompletedDateDesc();
            
            Map<String, Object> analytics = new HashMap<>();
            
            // Total profit and sales
            double totalProfit = allSales.stream()
                    .mapToDouble(s -> s.getTotalProfit() != null ? s.getTotalProfit() : 0.0)
                    .sum();
            double totalRevenue = allSales.stream()
                    .mapToDouble(SalesHistory::getTotalAmount)
                    .sum();
            double totalCost = allSales.stream()
                    .mapToDouble(s -> s.getTotalCost() != null ? s.getTotalCost() : 0.0)
                    .sum();
            
            analytics.put("totalProfit", totalProfit);
            analytics.put("totalRevenue", totalRevenue);
            analytics.put("totalCost", totalCost);
            analytics.put("profitMargin", totalRevenue > 0 ? (totalProfit / totalRevenue) * 100 : 0);
            analytics.put("totalSales", allSales.size());
            
            // Today's profit
            LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
            LocalDateTime endOfToday = LocalDate.now().atTime(23, 59, 59);
            double todayProfit = allSales.stream()
                    .filter(s -> s.getCompletedDate().isAfter(startOfToday) && 
                               s.getCompletedDate().isBefore(endOfToday))
                    .mapToDouble(s -> s.getTotalProfit() != null ? s.getTotalProfit() : 0.0)
                    .sum();
            analytics.put("todayProfit", todayProfit);
            
            // This month's profit
            LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            double monthProfit = allSales.stream()
                    .filter(s -> s.getCompletedDate().isAfter(startOfMonth))
                    .mapToDouble(s -> s.getTotalProfit() != null ? s.getTotalProfit() : 0.0)
                    .sum();
            analytics.put("monthProfit", monthProfit);
            
            // Category-wise profit
            Map<String, Double> categoryProfit = calculateCategoryProfit(allSales);
            analytics.put("categoryProfit", categoryProfit);
            
            // Daily profit for last 30 days
            Map<String, Double> dailyProfit = calculateDailyProfit(allSales, 30);
            analytics.put("dailyProfit", dailyProfit);
            
            // Monthly profit for last 12 months
            Map<String, Double> monthlyProfit = calculateMonthlyProfit(allSales, 12);
            analytics.put("monthlyProfit", monthlyProfit);
            
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    // Get profit by date range
    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getProfitByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);
            
            List<SalesHistory> sales = salesHistoryRepository.findAllByOrderByCompletedDateDesc()
                    .stream()
                    .filter(s -> s.getCompletedDate().isAfter(start) && 
                               s.getCompletedDate().isBefore(end))
                    .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            double totalProfit = sales.stream()
                    .mapToDouble(s -> s.getTotalProfit() != null ? s.getTotalProfit() : 0.0)
                    .sum();
            double totalRevenue = sales.stream()
                    .mapToDouble(SalesHistory::getTotalAmount)
                    .sum();
            
            result.put("totalProfit", totalProfit);
            result.put("totalRevenue", totalRevenue);
            result.put("totalSales", sales.size());
            result.put("categoryProfit", calculateCategoryProfit(sales));
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    // Helper method to calculate category-wise profit
    private Map<String, Double> calculateCategoryProfit(List<SalesHistory> sales) {
        Map<String, Double> categoryProfit = new HashMap<>();
        
        for (SalesHistory sale : sales) {
            try {
                if (sale.getProductDetails() != null && !sale.getProductDetails().isEmpty()) {
                    // Parse simple JSON format: [{"productId":1,"productName":"Camera","quantity":2,"price":5000.0}]
                    String json = sale.getProductDetails();
                    String[] items = json.substring(1, json.length() - 1).split("\\},\\{");
                    
                    double saleProfit = sale.getTotalProfit() != null ? sale.getTotalProfit() : 0.0;
                    int itemCount = items.length;
                    
                    for (String item : items) {
                        String cleanItem = item.replace("{", "").replace("}", "");
                        String[] fields = cleanItem.split(",");
                        
                        String productName = "Unknown";
                        for (String field : fields) {
                            if (field.contains("productName")) {
                                productName = field.split(":")[1].replace("\"", "").trim();
                                break;
                            }
                        }
                        
                        // Distribute profit equally among products
                        double itemProfit = saleProfit / itemCount;
                        categoryProfit.merge(productName, itemProfit, Double::sum);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return categoryProfit;
    }
    
    // Helper method to calculate daily profit
    private Map<String, Double> calculateDailyProfit(List<SalesHistory> sales, int days) {
        Map<String, Double> dailyProfit = new HashMap<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < days; i++) {
            LocalDate date = today.minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            
            double profit = sales.stream()
                    .filter(s -> s.getCompletedDate().isAfter(start) && 
                               s.getCompletedDate().isBefore(end))
                    .mapToDouble(s -> s.getTotalProfit() != null ? s.getTotalProfit() : 0.0)
                    .sum();
            
            dailyProfit.put(date.toString(), profit);
        }
        
        return dailyProfit;
    }
    
    // Helper method to calculate monthly profit
    private Map<String, Double> calculateMonthlyProfit(List<SalesHistory> sales, int months) {
        Map<String, Double> monthlyProfit = new HashMap<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < months; i++) {
            LocalDate month = today.minusMonths(i);
            LocalDateTime start = month.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            LocalDateTime end = month.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
            
            double profit = sales.stream()
                    .filter(s -> s.getCompletedDate().isAfter(start) && 
                               s.getCompletedDate().isBefore(end))
                    .mapToDouble(s -> s.getTotalProfit() != null ? s.getTotalProfit() : 0.0)
                    .sum();
            
            String monthKey = month.getYear() + "-" + String.format("%02d", month.getMonthValue());
            monthlyProfit.put(monthKey, profit);
        }
        
        return monthlyProfit;
    }
}
