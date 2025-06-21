package com.example.pcbuilderguideapp.models;

import java.io.Serializable;

public class SimpleCartItem implements Serializable {
    private int productId;
    private int quantity;

    public SimpleCartItem(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
} 