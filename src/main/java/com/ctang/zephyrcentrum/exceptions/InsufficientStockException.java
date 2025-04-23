package com.ctang.zephyrcentrum.exceptions;

public class InsufficientStockException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final Long itemId;
    private final Integer requestedQuantity;
    private final Integer availableQuantity;
    
    public InsufficientStockException(Long itemId, Integer requestedQuantity, Integer availableQuantity) {
        super(String.format("Insufficient stock for item ID %d. Requested: %d, Available: %d", 
                itemId, requestedQuantity, availableQuantity));
        this.itemId = itemId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    public Long getItemId() {
        return itemId;
    }
    
    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }
    
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
} 