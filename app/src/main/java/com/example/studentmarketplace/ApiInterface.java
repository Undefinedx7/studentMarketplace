package com.example.studentmarketplace;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("users/{userId}")
    Call<UserInfo> getUserInfo(@Path("userId") int userId);
}
