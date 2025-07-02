package com.og.pcbuilderguideapp.models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

public class Product implements Serializable {
    @SerializedName("productId")
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

    @SerializedName("category")
    @Expose
    private Category category;

    @SerializedName("orderItems")
    @Expose
    private Object orderItems;

    // Add a public no-argument constructor
    public Product() {}

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
    public Category getCategory() { return category; }
    public Object getOrderItems() { return orderItems; }

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
    public void setCategory(Category category) { this.category = category; }
    public void setOrderItems(Object orderItems) { this.orderItems = orderItems; }
}

class Category {
    @SerializedName("categoryId")
    private int categoryId;
    @SerializedName("categoryName")
    private String categoryName;

    // Add a public no-argument constructor
    public Category() {}

    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
} 