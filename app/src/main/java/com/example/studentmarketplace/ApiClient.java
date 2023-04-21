package com.example.studentmarketplace;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.studentmarketplace.ApiClientCallback.*;

//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/*public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:3000";
    private static Retrofit retrofit;
    private static ApiClient instance;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    private ApiClient() {
        retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void authenticateUser(String email, String password, ApiResponseListener listener) {
        AuthApi authApi = getRetrofitInstance().create(AuthApi.class);
        Call<ApiResponse> call = authApi.authenticate(email, password);

        Log.d("ApiClient", "Request: " + call.request().toString());

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        Log.d("ApiClient", "Response: " + apiResponse.toString());
                        if (apiResponse.isSuccess()) {
                            listener.onSuccess(apiResponse);
                        } else {
                            listener.onError(new ApiClientCallback.ApiError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Unknown error"));
                        }
                    } else {
                        listener.onError(new ApiClientCallback.ApiError("Response body is null"));
                    }
                } else {
                    listener.onError(new ApiClientCallback.ApiError("Failed to authenticate user"));
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("ApiClient", "Error authenticating user: " + t.getMessage());
                listener.onError(new ApiError(t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    public interface AuthApi {
        @FormUrlEncoded
        @POST("auth")
        Call<ApiResponse> authenticate(
                @Field("email") String email,
                @Field("password") String password
        );
    }



    public interface ItemApi {
        @FormUrlEncoded
        @POST("items")
        Call<ApiResponse> addItem(
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
*/
import okhttp3.*;
import org.json.*;

import java.io.IOException;


public class ApiClient {
    String json = "{\"key\": \"value\"}";
    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
    private static final String BASE_URL = "http://10.0.2.2:3000";

    private static ApiClient instance;
    private final OkHttpClient client;

    private ApiClient() {
        client = new OkHttpClient();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public void authenticateUser(String email, String password, final ApiResponseListener listener) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError(new ApiError("Error creating JSON request body"));
            return;
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "/auth")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onError(new ApiError("Error connecting to server"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    listener.onError(new ApiError("Authentication failed: " + response.message()));
                    return;
                }

                try {
                    JSONObject responseBody = new JSONObject(response.body().string());

                    boolean success = responseBody.getBoolean("success");
                    String message = responseBody.getString("message");

                    if (success) {
                        JSONObject data = responseBody.getJSONObject("data");

                        int id = data.getInt("id");
                        String name = data.getString("name");
                        String token = data.getString("token");



                        listener.onSuccess(new ApiResponse(id, name, email, token));
                    } else {
                        listener.onError(new ApiError(message));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError(new ApiError("Error parsing JSON response"));
                }
            }
        });
    }

    public interface ApiResponseListener {
        void onSuccess(ApiResponse response);
        void onError(ApiError error);
    }

    public static class ApiError {
        private final String message;

        public ApiError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ApiResponse {
        private final int id;
        private final String name;
        private final String email;
        private final String token;

        public ApiResponse(int id, String name, String email, String token) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.token = token;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getToken() {
            return token;
        }
    }


}
