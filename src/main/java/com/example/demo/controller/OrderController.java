package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderRequest;
import com.example.demo.entity.Inventory;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.SalesHistory;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SalesHistoryRepository;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SalesHistoryRepository salesHistoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    // Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
        Optional<Order> orderData = orderRepository.findById(id);
        return orderData.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create new order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            // Create order entity
            Order order = new Order();
            order.setCustomerName(orderRequest.getCustomerName());
            order.setCustomerEmail(orderRequest.getCustomerEmail());
            order.setCustomerPhone(orderRequest.getCustomerPhone());
            order.setShippingAddress(orderRequest.getShippingAddress());
            order.setNotes(orderRequest.getNotes());
            order.setTotalAmount(orderRequest.getTotalAmount());
            order.setPaymentMethod(orderRequest.getPaymentMethod());
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(orderRequest.getStatus() != null ? orderRequest.getStatus() : "PENDING");
            
            // Calculate product count from items
            int totalQuantity = 0;
            if (orderRequest.getItems() != null && !orderRequest.getItems().isEmpty()) {
                for (OrderRequest.OrderItemDTO itemDTO : orderRequest.getItems()) {
                    OrderItem item = new OrderItem();
                    item.setProductId(itemDTO.getProductId());
                    item.setProductName(itemDTO.getProductName());
                    item.setQuantity(itemDTO.getQuantity());
                    item.setPrice(itemDTO.getPrice());
                    order.addItem(item);
                    totalQuantity += itemDTO.getQuantity();
                }
            }
            order.setProductCount(totalQuantity);
            
            Order savedOrder = orderRepository.save(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable("id") Long id, @RequestBody String status) {
        Optional<Order> orderData = orderRepository.findById(id);
        
        if (orderData.isPresent()) {
            Order order = orderData.get();
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);
            return ResponseEntity.ok(updatedOrder);
        }
        
        return ResponseEntity.notFound().build();
    }

    // Update entire order
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") Long id, @RequestBody java.util.Map<String, Object> updates) {
        Optional<Order> orderData = orderRepository.findById(id);
        
        if (orderData.isPresent()) {
            Order order = orderData.get();
            String oldStatus = order.getStatus();
            
            // Update only the fields that are provided
            if (updates.containsKey("status")) {
                order.setStatus((String) updates.get("status"));
            }
            if (updates.containsKey("customerName")) {
                order.setCustomerName((String) updates.get("customerName"));
            }
            if (updates.containsKey("customerEmail")) {
                order.setCustomerEmail((String) updates.get("customerEmail"));
            }
            if (updates.containsKey("customerPhone")) {
                order.setCustomerPhone((String) updates.get("customerPhone"));
            }
            if (updates.containsKey("shippingAddress")) {
                order.setShippingAddress((String) updates.get("shippingAddress"));
            }
            if (updates.containsKey("productCount")) {
                order.setProductCount((Integer) updates.get("productCount"));
            }
            if (updates.containsKey("totalAmount")) {
                Object amount = updates.get("totalAmount");
                if (amount instanceof Integer) {
                    order.setTotalAmount(((Integer) amount).doubleValue());
                } else if (amount instanceof Double) {
                    order.setTotalAmount((Double) amount);
                }
            }
            if (updates.containsKey("paymentMethod")) {
                order.setPaymentMethod((String) updates.get("paymentMethod"));
            }
            if (updates.containsKey("notes")) {
                order.setNotes((String) updates.get("notes"));
            }
            
            Order updatedOrder = orderRepository.save(order);
            
            // If status changed to COMPLETED, save to sales_history with profit calculation
            if (!oldStatus.equals("COMPLETED") && updatedOrder.getStatus().equals("COMPLETED")) {
                try {
                    // Reduce inventory quantities for each product in the order
                    if (updatedOrder.getItems() != null && !updatedOrder.getItems().isEmpty()) {
                        for (OrderItem item : updatedOrder.getItems()) {
                            try {
                                // Find inventory by productId
                                Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(item.getProductId());
                                if (inventoryOpt.isPresent()) {
                                    Inventory inventory = inventoryOpt.get();
                                    int currentQuantity = inventory.getQuantity();
                                    int orderQuantity = item.getQuantity();
                                    
                                    // Reduce quantity
                                    int newQuantity = currentQuantity - orderQuantity;
                                    if (newQuantity < 0) {
                                        System.err.println("Warning: Inventory for product " + item.getProductName() + 
                                            " (ID: " + item.getProductId() + ") will go negative. Current: " + 
                                            currentQuantity + ", Ordered: " + orderQuantity);
                                        newQuantity = 0; // Set to 0 instead of negative
                                    }
                                    
                                    inventory.setQuantity(newQuantity);
                                    inventoryRepository.save(inventory);
                                    System.out.println("Updated inventory for " + item.getProductName() + 
                                        ": " + currentQuantity + " -> " + newQuantity);
                                } else {
                                    System.err.println("Warning: No inventory found for product ID: " + item.getProductId());
                                }
                            } catch (Exception e) {
                                System.err.println("Error updating inventory for product ID " + item.getProductId() + ": " + e.getMessage());
                                // Continue processing other items even if one fails
                            }
                        }
                    }
                    
                    // Save to sales history
                    SalesHistory salesHistory = new SalesHistory(updatedOrder);
                    
                    // Calculate profit
                    double totalCost = 0.0;
                    double totalProfit = 0.0;
                    
                    if (updatedOrder.getItems() != null && !updatedOrder.getItems().isEmpty()) {
                        for (OrderItem item : updatedOrder.getItems()) {
                            Optional<Product> productOpt = productRepository.findById(item.getProductId());
                            if (productOpt.isPresent()) {
                                Product product = productOpt.get();
                                // Use costPrice if available, otherwise use originalPrice as cost
                                // If neither is available, assume cost is 60% of selling price
                                double costPrice;
                                if (product.getCostPrice() != null && product.getCostPrice() > 0) {
                                    costPrice = product.getCostPrice();
                                } else if (product.getOriginalPrice() != null && product.getOriginalPrice() > 0) {
                                    costPrice = product.getOriginalPrice();
                                } else {
                                    costPrice = item.getPrice() * 0.6; // Assume 40% profit margin
                                }
                                
                                double sellingPrice = item.getPrice();
                                int quantity = item.getQuantity();
                                
                                totalCost += (costPrice * quantity);
                                totalProfit += ((sellingPrice - costPrice) * quantity);
                            }
                        }
                    }
                    
                    salesHistory.setTotalCost(totalCost);
                    salesHistory.setTotalProfit(totalProfit);
                    salesHistoryRepository.save(salesHistory);
                } catch (Exception e) {
                    System.err.println("Error saving to sales history: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            return ResponseEntity.ok(updatedOrder);
        }
        
        return ResponseEntity.notFound().build();
    }

    // Delete order
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("id") Long id) {
        try {
            orderRepository.deleteById(id);
            return ResponseEntity.ok(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable("status") String status) {
        try {
            // Assuming you add this method to the repository
            List<Order> orders = orderRepository.findAll().stream()
                    .filter(order -> order.getStatus().equalsIgnoreCase(status))
                    .toList();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
