package com.ctang.zephyrcentrum.services;

import java.util.List;

import com.ctang.zephyrcentrum.models.Item;

public interface ItemService {
    
    List<Item> getAllItems();
    
    Item getItemById(Long id);
    
    List<Item> getItemsByName(String name);
    
    List<Item> getAvailableItems();
    
    Item createItem(Item item);
    
    Item updateItem(Long id, Item item);
    
    void deleteItem(Long id);
    
    boolean isItemInStock(Long itemId, Integer quantity);
    
    void updateStock(Long itemId, Integer quantity);
} 