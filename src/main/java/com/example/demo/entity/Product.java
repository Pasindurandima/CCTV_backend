package com.example.demo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String brand;
    
    @Column(nullable = false)
    private Double price;
    
    private Double originalPrice;
    
    private Double costPrice; // Cost/wholesale price for profit calculation
    
    @Column(nullable = false)
    private String category;
    
    @Column(length = 500)
    private String shortDesc;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_features", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "feature", length = 500)
    private List<String> features;
    
    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imageUrl;
    
    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imageUrl1;
    
    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imageUrl2;
    
    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String imageUrl3;
    
    // Constructors
    public Product() {
    }
    
    public Product(String name, String brand, Double price, Double originalPrice, 
                   String category, String shortDesc, List<String> features, String imageUrl) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.originalPrice = originalPrice;
        this.category = category;
        this.shortDesc = shortDesc;
        this.features = features;
        this.imageUrl = imageUrl;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Double getOriginalPrice() {
        return originalPrice;
    }
    
    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }
    
    public Double getCostPrice() {
        return costPrice;
    }
    
    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getShortDesc() {
        return shortDesc;
    }
    
    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }
    
    public List<String> getFeatures() {
        return features;
    }
    
    public void setFeatures(List<String> features) {
        this.features = features;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getImageUrl1() {
        return imageUrl1;
    }
    
    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }
    
    public String getImageUrl2() {
        return imageUrl2;
    }
    
    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }
    
    public String getImageUrl3() {
        return imageUrl3;
    }
    
    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }
    
    // Helper method to get all image URLs as an array
    @Transient
    @JsonProperty("imageUrls")
    public java.util.List<String> getImageUrls() {
        java.util.List<String> urls = new java.util.ArrayList<>();
        if (imageUrl1 != null && !imageUrl1.isEmpty()) urls.add(imageUrl1);
        if (imageUrl2 != null && !imageUrl2.isEmpty()) urls.add(imageUrl2);
        if (imageUrl3 != null && !imageUrl3.isEmpty()) urls.add(imageUrl3);
        // Fallback to legacy imageUrl if no new images
        if (urls.isEmpty() && imageUrl != null && !imageUrl.isEmpty()) {
            urls.add(imageUrl);
        }
        return urls;
    }
}
