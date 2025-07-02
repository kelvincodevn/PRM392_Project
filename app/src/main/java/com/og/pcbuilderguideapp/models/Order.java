package com.og.pcbuilderguideapp.models;

import com.og.pcbuilderguideapp.utils.DateAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Order {
    @SerializedName("$id")
    private String id;

    @SerializedName("orderId")
    private int orderId;

    @SerializedName("customerId")
    private int customerId;

    @JsonAdapter(DateAdapter.class)
    @SerializedName("orderDate")
    private Date orderDate;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("shippingFee")
    private double shippingFee;

    @SerializedName("finalAmount")
    private double finalAmount;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("shippingAddress")
    private String shippingAddress;

    @SerializedName("staffId")
    private int staffId;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @JsonAdapter(DateAdapter.class)
    @SerializedName("deliveredAt")
    private Date deliveredAt;

    @SerializedName("isDeleted")
    private boolean isDeleted;

    @JsonAdapter(DateAdapter.class)
    @SerializedName("createdAt")
    private Date createdAt;

    @JsonAdapter(DateAdapter.class)
    @SerializedName("updatedAt")
    private Date updatedAt;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerPhone")
    private String customerPhone;

    @SerializedName("staffName")
    private String staffName;

    @SerializedName("orderItems")
    private List<OrderItem> orderItems;

    // Getters
    public String getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public Date getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public double getShippingFee() { return shippingFee; }
    public double getFinalAmount() { return finalAmount; }
    public String getOrderStatus() { return orderStatus; }
    public String getShippingAddress() { return shippingAddress; }
    public int getStaffId() { return staffId; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public Date getDeliveredAt() { return deliveredAt; }
    public boolean isDeleted() { return isDeleted; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getStaffName() { return staffName; }
    public List<OrderItem> getOrderItems() { return orderItems; }
} 