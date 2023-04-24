package com.example.studentmarketplace;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("users/{userId}")
    Call<UserInfo> getUserInfo(@Path("userId") int userId);

    @GET("/categories")
    Call<List<String>> getCategories();

    @GET("/status")
    Call<List<String>> getStatus();

    @Multipart
    @POST("add_product")
    Call<ApiClient.ApiResponse> addProduct(@PartMap Map<String, RequestBody> productData, @Part List<MultipartBody.Part> productImages);

}
