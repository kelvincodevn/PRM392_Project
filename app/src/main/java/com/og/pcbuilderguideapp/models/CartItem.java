package com.og.pcbuilderguideapp.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int cartItemId;
    private int productId;
    private int quantity;
    private String addedAt;
    private Product product;

    public int getCartItemId() { return cartItemId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getAddedAt() { return addedAt; }
    public Product getProduct() { return product; }

    public void setCartItemId(int cartItemId) { this.cartItemId = cartItemId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setAddedAt(String addedAt) { this.addedAt = addedAt; }
    public void setProduct(Product product) { this.product = product; }
} 