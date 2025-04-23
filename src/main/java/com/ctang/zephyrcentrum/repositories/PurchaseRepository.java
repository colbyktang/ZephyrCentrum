package com.ctang.zephyrcentrum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ctang.zephyrcentrum.models.Purchase;
import com.ctang.zephyrcentrum.models.User;
import com.ctang.zephyrcentrum.models.Item;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    
    List<Purchase> findByUser(User user);
    
    List<Purchase> findByUserOrderByPurchaseDateDesc(User user);
    
    List<Purchase> findByItem(Item item);
} 