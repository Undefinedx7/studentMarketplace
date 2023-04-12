package com.example.studentmarketplace;

import java.util.Date;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class UserInfo {
    private int userId;
    private String username;
    private String email;
    private String password;
    private String token;
    private int locationId;
    private Date createdAt;
    private Date uploadedAt;

    public UserInfo(int userId, String username, String email, String password, String token, int locationId, Date createdAt, Date uploadedAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.token = token;
        this.locationId = locationId;
        this.createdAt = createdAt;
        this.uploadedAt = uploadedAt;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public int getLocationId() {
        return locationId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }
}

