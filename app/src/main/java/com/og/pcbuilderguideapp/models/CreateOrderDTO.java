package com.og.pcbuilderguideapp.models;

import java.util.List;

public class CreateOrderDTO {
    private String shippingAddress;
    private String paymentMethod;
    private List<CreateOrderItemDTO> orderItems;

    public CreateOrderDTO(String shippingAddress, String paymentMethod, List<CreateOrderItemDTO> orderItems) {
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.orderItems = orderItems;
    }

    public String getShippingAddress() { return shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public List<CreateOrderItemDTO> getOrderItems() { return orderItems; }

    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setOrderItems(List<CreateOrderItemDTO> orderItems) { this.orderItems = orderItems; }
} 