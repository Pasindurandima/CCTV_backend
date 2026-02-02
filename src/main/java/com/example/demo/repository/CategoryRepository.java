package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findAllByIsActiveTrueOrderByDisplayOrderAsc();
    
    List<Category> findAllByOrderByDisplayOrderAsc();
    
    Optional<Category> findByName(String name);
    
    boolean existsByName(String name);
}
