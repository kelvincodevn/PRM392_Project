package com.og.pcbuilderguideapp.models;

import java.util.List;

public class OrderListResponse {
    private List<Order> orders;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
} 