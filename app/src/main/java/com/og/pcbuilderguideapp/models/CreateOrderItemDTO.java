package com.og.pcbuilderguideapp.models;

public class CreateOrderItemDTO {
    private int productId;
    private int quantity;
    private int thirdPartyId;

    public CreateOrderItemDTO(int productId, int quantity, int thirdPartyId) {
        this.productId = productId;
        this.quantity = quantity;
        this.thirdPartyId = thirdPartyId;
    }

    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public int getThirdPartyId() { return thirdPartyId; }

    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setThirdPartyId(int thirdPartyId) { this.thirdPartyId = thirdPartyId; }
} 