package com.example.pcbuilderguideapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderListResponse {
    @SerializedName("$values")
    private List<Order> values;

    public List<Order> getValues() {
        return values;
    }

    public void setValues(List<Order> values) {
        this.values = values;
    }
} 