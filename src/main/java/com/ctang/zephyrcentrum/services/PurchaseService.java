package com.ctang.zephyrcentrum.services;

import java.util.List;

import com.ctang.zephyrcentrum.models.Purchase;
import com.ctang.zephyrcentrum.exceptions.InsufficientStockException;

public interface PurchaseService {
    
    List<Purchase> getAllPurchases();
    
    Purchase getPurchaseById(Long id);
    
    List<Purchase> getPurchasesByUserId(Long userId);
    
    List<Purchase> getPurchasesByItemId(Long itemId);
    
    Purchase buyItem(Long userId, Long itemId, Integer quantity) throws InsufficientStockException;
} 