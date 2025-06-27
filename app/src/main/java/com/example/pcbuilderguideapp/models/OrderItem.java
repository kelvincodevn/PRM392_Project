package com.example.pcbuilderguideapp.models;

import com.example.pcbuilderguideapp.utils.DateAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class OrderItem {
    @SerializedName("$id")
    private String id;

    @SerializedName("orderItemId")
    private int orderItemId;

    @SerializedName("orderId")
    private int orderId;

    @SerializedName("productId")
    private int productId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("priceAtOrder")
    private double unitPrice;

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("thirdPartyId")
    private int thirdPartyId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("productImage")
    private String productImage;

    @SerializedName("thirdPartyName")
    private String thirdPartyName;

    @JsonAdapter(DateAdapter.class)
    @SerializedName("createdAt")
    private Date createdAt;

    @JsonAdapter(DateAdapter.class)
    @SerializedName("updatedAt")
    private Date updatedAt;

    // Getters
    public String getId() { return id; }
    public int getOrderItemId() { return orderItemId; }
    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotalPrice() { return totalPrice; }
    public int getThirdPartyId() { return thirdPartyId; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; }
    public String getThirdPartyName() { return thirdPartyName; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
} 