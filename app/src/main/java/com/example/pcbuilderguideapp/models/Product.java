package com.example.pcbuilderguideapp.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    private int id;

    @SerializedName("productName")
    private String name;

    @SerializedName("thirdParty")
    private ThirdParty thirdParty;

    @SerializedName("price")
    private double price;

    @SerializedName("stockQuantity")
    private int quantity;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public ThirdParty getThirdParty() { return thirdParty; }
    public String getCompanyName() {
        return thirdParty != null ? thirdParty.getCompanyName() : "";
    }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setThirdParty(ThirdParty thirdParty) { this.thirdParty = thirdParty; }
    public void setCompanyName(String companyName) {
        if (this.thirdParty == null) {
            this.thirdParty = new ThirdParty();
        }
        this.thirdParty.setCompanyName(companyName);
    }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
} 