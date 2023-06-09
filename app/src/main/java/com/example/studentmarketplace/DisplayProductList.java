package com.example.studentmarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DisplayProductList extends AppCompatActivity {
    private RecyclerView productRecyclerView;

    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        productRecyclerView = findViewById(R.id.product_grid);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        productRecyclerView = findViewById(R.id.product_grid);
        productAdapter = new ProductAdapter(this, productList);
        productRecyclerView.setAdapter(productAdapter);


        categoryName = getIntent().getStringExtra("category");
        TextView categoryNameTextView = findViewById(R.id.category_name);
        categoryNameTextView.setText(categoryName);

        fetchProducts(categoryName);

    }



    private void fetchProducts(String category) {
        String url = "http://10.0.2.2:3000/products-single";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (category != null) {
            urlBuilder.addQueryParameter("category", category);
        }
        String finalUrl = urlBuilder.build().toString();
        Log.d("Final URL", finalUrl);

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("HTTP_REQUEST", "Failed to fetch products: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        productList = new ArrayList<>();

                        JSONObject responseObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = responseObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject productJson = jsonArray.getJSONObject(i);
                            String id = productJson.getString("id");
                            String title = productJson.getString("name");
                            String description = productJson.getString("description");
                            double price = productJson.getDouble("price");
                            String category = productJson.getString("category");
                            String userId = productJson.getString("sellerId");
                            String location = productJson.getString("location");
                            JSONArray imageUrlsJson = productJson.getJSONArray("image");
                            List<String> imageUrls = new ArrayList<>();
                            for (int j = 0; j < imageUrlsJson.length(); j++) {
                                String imageUrl = imageUrlsJson.getString(j);
                                imageUrl = imageUrl.replaceAll("\\\\", "");
                                imageUrl = imageUrl.replaceAll(" ", "");
                                imageUrl = imageUrl.replaceAll("\\\"]", "");
                                imageUrl = imageUrl.replaceAll("\"", "");

                                imageUrls.add(imageUrl);
                            }
                            Product product = new Product(id, title, description, price, category, userId, location, imageUrls);
                            productList.add(product);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                productAdapter.setProductList(productList);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("HTTP_REQUEST", "Failed to parse products JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("HTTP_REQUEST", "Failed to fetch products: " + response.code() + " - " + response.message());
                }
            }
        });
    }

}
