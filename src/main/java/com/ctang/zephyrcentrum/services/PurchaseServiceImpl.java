package com.ctang.zephyrcentrum.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctang.zephyrcentrum.exceptions.InsufficientStockException;
import com.ctang.zephyrcentrum.models.Item;
import com.ctang.zephyrcentrum.models.Purchase;
import com.ctang.zephyrcentrum.models.User;
import com.ctang.zephyrcentrum.repositories.ItemRepository;
import com.ctang.zephyrcentrum.repositories.PurchaseRepository;
import com.ctang.zephyrcentrum.repositories.UserRepository;

import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    public PurchaseServiceImpl(
            PurchaseRepository purchaseRepository,
            UserRepository userRepository,
            ItemRepository itemRepository,
            ItemService itemService) {
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    @Override
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    @Override
    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    @Override
    public List<Purchase> getPurchasesByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return List.of();
        }
        return purchaseRepository.findByUserOrderByPurchaseDateDesc(user);
    }

    @Override
    public List<Purchase> getPurchasesByItemId(Long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            return List.of();
        }
        return purchaseRepository.findByItem(item);
    }

    @Override
    @Transactional
    public Purchase buyItem(Long userId, Long itemId, Integer quantity) throws InsufficientStockException {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        // Validate item exists
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));
        
        // Check if there's enough stock
        if (!itemService.isItemInStock(itemId, quantity)) {
            throw new InsufficientStockException(itemId, quantity, item.getStock());
        }
        
        // Create purchase record
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setItem(item);
        purchase.setQuantity(quantity);
        purchase.setPurchasePrice(item.getPrice().multiply(java.math.BigDecimal.valueOf(quantity)));
        
        // Update stock
        itemService.updateStock(itemId, -quantity);
        
        // Save and return purchase
        return purchaseRepository.save(purchase);
    }
} 