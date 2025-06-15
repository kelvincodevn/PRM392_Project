package com.example.pcbuilderguideapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderResponse {
    @SerializedName("$id")
    private String id;

    @SerializedName("$values")
    private List<Order> orders;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "id='" + id + '\'' +
                ", orders=" + (orders != null ? orders.size() : "null") +
                '}';
    }
} 