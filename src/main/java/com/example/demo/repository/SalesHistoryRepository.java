package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.SalesHistory;

@Repository
public interface SalesHistoryRepository extends JpaRepository<SalesHistory, Long> {
    
    // Find by customer email
    List<SalesHistory> findByCustomerEmail(String customerEmail);
    
    // Find by customer name (case insensitive)
    List<SalesHistory> findByCustomerNameContainingIgnoreCase(String customerName);
    
    // Find by customer phone
    List<SalesHistory> findByCustomerPhone(String customerPhone);
    
    // Find by order ID
    SalesHistory findByOrderId(Long orderId);
    
    // Get all sales history ordered by completed date descending
    List<SalesHistory> findAllByOrderByCompletedDateDesc();
    
    // Search across multiple fields
    @Query("SELECT s FROM SalesHistory s WHERE " +
           "LOWER(s.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.customerEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "s.customerPhone LIKE CONCAT('%', :searchTerm, '%') OR " +
           "CAST(s.orderId AS string) LIKE CONCAT('%', :searchTerm, '%')")
    List<SalesHistory> searchSalesHistory(String searchTerm);
}
