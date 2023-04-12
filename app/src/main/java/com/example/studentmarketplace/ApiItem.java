package com.example.studentmarketplace;
import android.content.Context;

import com.example.studentmarketplace.ApiClientCallback.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ApiItem {

    private static final String BASE_URL = "http://10.0.2.2:3000";
    private static Retrofit retrofit;
    private static ApiItem instance;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static ApiItem getInstance(Context context) {
        if (instance == null) {
            instance = new ApiItem();
        }
        return instance;
    }

    public interface ItemApi {
        @FormUrlEncoded
        @POST("items")
        Call<ApiClientCallback.ApiResponse> addItem(
                @Field("title") String title,
                @Field("description") String description,
                @Field("price") double price,
                @Field("category") String category
        );
    }

    public void addItem(String title, String description, double price, String category, ApiClientCallback.ApiResponseListener listener) {
        ItemApi itemApi = getRetrofitInstance().create(ItemApi.class);
        Call<ApiResponse> call = itemApi.addItem(title, description, price, category);
        call.enqueue(new Callback<ApiClientCallback.ApiResponse>() {
            @Override
            public void onResponse(Call<ApiClientCallback.ApiResponse> call, Response<ApiClientCallback.ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiClientCallback.ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.isSuccess()) {
                        listener.onSuccess(apiResponse);
                    } else {
                        listener.onError(new ApiClientCallback.ApiError(apiResponse != null ? apiResponse.getMessage() : "Unknown error"));
                    }
                } else {
                    listener.onError(new ApiClientCallback.ApiError("Failed to add item"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                listener.onError(new ApiClientCallback.ApiError(t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }
}
