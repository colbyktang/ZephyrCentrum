package com.ctang.zephyrcentrum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ctang.zephyrcentrum.models.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    Optional<Item> findByName(String name);
    
    List<Item> findByNameContainingIgnoreCase(String name);
    
    List<Item> findByStockGreaterThan(Integer minimumStock);
} 