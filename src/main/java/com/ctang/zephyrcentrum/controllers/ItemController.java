package com.ctang.zephyrcentrum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ctang.zephyrcentrum.exceptions.InsufficientStockException;
import com.ctang.zephyrcentrum.models.Item;
import com.ctang.zephyrcentrum.models.Purchase;
import com.ctang.zephyrcentrum.services.ItemService;
import com.ctang.zephyrcentrum.services.PurchaseService;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/items")
@CrossOrigin(maxAge = 3600)
public class ItemController {

    private final ItemService itemService;
    private final PurchaseService purchaseService;
    private final Bucket bucket;

    public ItemController(ItemService itemService, PurchaseService purchaseService, Bucket bucket) {
        this.itemService = itemService;
        this.purchaseService = purchaseService;
        this.bucket = bucket;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            List<Item> items = itemService.getAllItems();
            return items.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            Item item = itemService.getItemById(id);
            return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String name) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            List<Item> items = itemService.getItemsByName(name);
            return items.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<Item>> getAvailableItems() {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            List<Item> items = itemService.getAvailableItems();
            return items.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            Item newItem = itemService.createItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @Valid @RequestBody Item item) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            Item updatedItem = itemService.updateItem(id, item);
            return updatedItem == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            Item item = itemService.getItemById(id);
            if (item == null) {
                return ResponseEntity.notFound().build();
            }
            itemService.deleteItem(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{itemId}/buy")
    public ResponseEntity<?> buyItem(
            @PathVariable Long itemId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        
        try {
            Purchase purchase = purchaseService.buyItem(userId, itemId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(purchase);
        } catch (InsufficientStockException e) {
            Map<String, Object> error = Map.of(
                "error", "Insufficient stock",
                "itemId", e.getItemId(),
                "requestedQuantity", e.getRequestedQuantity(),
                "availableQuantity", e.getAvailableQuantity()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/{userId}/purchases")
    public ResponseEntity<List<Purchase>> getUserPurchases(@PathVariable Long userId) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            List<Purchase> purchases = purchaseService.getPurchasesByUserId(userId);
            return purchases.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(purchases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 