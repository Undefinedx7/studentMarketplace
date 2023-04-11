package com.example.studentmarketplace;

import com.google.android.gms.maps.model.LatLng;

public class Item {
    private String name;
    private String description;
    private int imageResourceId;
    private double price;
    private double latitude;
    private double longitude;


    public Item(String name, String description, int imageResourceId, double price, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public double getPrice() {
        return price;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }
}

