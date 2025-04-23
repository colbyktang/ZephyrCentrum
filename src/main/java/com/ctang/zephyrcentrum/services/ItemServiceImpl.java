package com.ctang.zephyrcentrum.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctang.zephyrcentrum.models.Item;
import com.ctang.zephyrcentrum.repositories.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    @Override
    public List<Item> getItemsByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Item> getAvailableItems() {
        return itemRepository.findByStockGreaterThan(0);
    }

    @Override
    @Transactional
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Long id, Item item) {
        Optional<Item> existingItemOpt = itemRepository.findById(id);
        if (existingItemOpt.isPresent()) {
            Item existingItem = existingItemOpt.get();
            existingItem.setName(item.getName());
            existingItem.setPrice(item.getPrice());
            existingItem.setDescription(item.getDescription());
            existingItem.setStock(item.getStock());
            return itemRepository.save(existingItem);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public boolean isItemInStock(Long itemId, Integer quantity) {
        Item item = getItemById(itemId);
        return item != null && item.getStock() >= quantity;
    }

    @Override
    @Transactional
    public void updateStock(Long itemId, Integer quantity) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            item.setStock(item.getStock() + quantity);
            itemRepository.save(item);
        }
    }
} 