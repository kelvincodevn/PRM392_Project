package com.example.pcbuilderguideapp.models;

import java.util.List;

public class Cart {
    private int cartId;
    private int userId;
    private String createdAt;
    private String updatedAt;
    private List<CartItem> cartItems;

    public int getCartId() { return cartId; }
    public int getUserId() { return userId; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public List<CartItem> getCartItems() { return cartItems; }

    public void setCartId(int cartId) { this.cartId = cartId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }
} 