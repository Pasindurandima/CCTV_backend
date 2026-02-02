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

import com.example.demo.entity.Inventory;
import com.example.demo.repository.InventoryRepository;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class InventoryController {
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    // Get all inventory items
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        try {
            List<Inventory> inventory = inventoryRepository.findAll();
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    // Get inventory by ID
    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryRepository.findById(id);
        return inventory.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Get inventory by product ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable Long productId) {
        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        return inventory.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Get low stock items
    @GetMapping("/low-stock")
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        try {
            List<Inventory> allInventory = inventoryRepository.findAll();
            List<Inventory> lowStock = allInventory.stream()
                    .filter(inv -> inv.getQuantity() <= inv.getReorderLevel())
                    .toList();
            return ResponseEntity.ok(lowStock);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    // Create new inventory item
    @PostMapping
    public ResponseEntity<?> createInventory(@RequestBody Inventory inventory) {
        try {
            if (inventory.getProductId() == null) {
                return ResponseEntity.badRequest().body("Product ID is required");
            }
            if (inventory.getProductName() == null || inventory.getProductName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Product name is required");
            }
            if (inventory.getQuantity() == null || inventory.getQuantity() < 0) {
                return ResponseEntity.badRequest().body("Valid quantity is required");
            }
            if (inventory.getUnitPrice() == null || inventory.getUnitPrice() <= 0) {
                return ResponseEntity.badRequest().body("Valid unit price is required");
            }
            
            inventory.setLastUpdated(LocalDateTime.now());
            Inventory savedInventory = inventoryRepository.save(inventory);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedInventory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating inventory: " + e.getMessage());
        }
    }
    
    // Update inventory
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable Long id, @RequestBody Inventory inventoryDetails) {
        try {
            Optional<Inventory> inventoryOptional = inventoryRepository.findById(id);
            
            if (inventoryOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Inventory inventory = inventoryOptional.get();
            
            if (inventoryDetails.getQuantity() != null) {
                inventory.setQuantity(inventoryDetails.getQuantity());
            }
            if (inventoryDetails.getReorderLevel() != null) {
                inventory.setReorderLevel(inventoryDetails.getReorderLevel());
            }
            if (inventoryDetails.getUnitPrice() != null) {
                inventory.setUnitPrice(inventoryDetails.getUnitPrice());
            }
            
            inventory.setLastUpdated(LocalDateTime.now());
            Inventory updatedInventory = inventoryRepository.save(inventory);
            
            return ResponseEntity.ok(updatedInventory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating inventory: " + e.getMessage());
        }
    }
    
    // Delete inventory
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long id) {
        try {
            if (!inventoryRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            inventoryRepository.deleteById(id);
            return ResponseEntity.ok("Inventory deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting inventory: " + e.getMessage());
        }
    }
    
    // Adjust stock (add or remove quantity)
    @PostMapping("/{id}/adjust")
    public ResponseEntity<?> adjustStock(@PathVariable Long id, @RequestBody java.util.Map<String, Object> adjustment) {
        try {
            Optional<Inventory> inventoryOptional = inventoryRepository.findById(id);
            
            if (inventoryOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Inventory inventory = inventoryOptional.get();
            String type = (String) adjustment.get("type"); // "add" or "remove"
            Integer amount = (Integer) adjustment.get("amount");
            String reason = (String) adjustment.get("reason");
            
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Valid amount is required");
            }
            
            int newQuantity = inventory.getQuantity();
            if ("add".equals(type)) {
                newQuantity += amount;
            } else if ("remove".equals(type)) {
                newQuantity -= amount;
                if (newQuantity < 0) {
                    return ResponseEntity.badRequest().body("Insufficient stock");
                }
            } else {
                return ResponseEntity.badRequest().body("Type must be 'add' or 'remove'");
            }
            
            inventory.setQuantity(newQuantity);
            inventory.setLastUpdated(LocalDateTime.now());
            Inventory updatedInventory = inventoryRepository.save(inventory);
            
            return ResponseEntity.ok(updatedInventory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adjusting stock: " + e.getMessage());
        }
    }
    
    // Bulk update reorder levels
    @PutMapping("/bulk/reorder-level")
    public ResponseEntity<?> bulkUpdateReorderLevel(@RequestBody java.util.Map<String, Integer> updates) {
        try {
            int updated = 0;
            for (java.util.Map.Entry<String, Integer> entry : updates.entrySet()) {
                Long id = Long.parseLong(entry.getKey());
                Integer reorderLevel = entry.getValue();
                
                Optional<Inventory> inventoryOptional = inventoryRepository.findById(id);
                if (inventoryOptional.isPresent()) {
                    Inventory inventory = inventoryOptional.get();
                    inventory.setReorderLevel(reorderLevel);
                    inventory.setLastUpdated(LocalDateTime.now());
                    inventoryRepository.save(inventory);
                    updated++;
                }
            }
            return ResponseEntity.ok("Updated " + updated + " inventory items");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error bulk updating: " + e.getMessage());
        }
    }
}
