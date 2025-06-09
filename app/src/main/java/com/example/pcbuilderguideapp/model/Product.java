package com.example.pcbuilderguideapp.model;

public class Product {
    public String name;
    public String price;
    public String details;
    public int imageResId;

    public Product(String name, String price, String details, int imageResId) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.imageResId = imageResId;
    }
} 