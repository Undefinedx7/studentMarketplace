package com.example.studentmarketplace;

import com.google.gson.Gson;

import java.util.List;

public class Product {
    private String title;
    private String description;
    private double price;
    private String category;
    private String userId;
    private String location;
    private List<String> imageUrls;

    public Product(String title, String description, double price, String category, String userId, String location, List<String> imageUrls) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.userId = userId;
        this.location = location;
        this.imageUrls = imageUrls;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getUserId() {
        return userId;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
