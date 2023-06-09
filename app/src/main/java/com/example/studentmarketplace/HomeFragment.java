package com.example.studentmarketplace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

        private RecyclerView todayRecView, propertyRecView, electronicsRecView, bookRecView;
        private List<Product> productList, logementProductList, electronicsList, booksList;
        private ProductAdapter adapterToday, adapterProperty, adapterElectronics, adapterBooks;
        private OkHttpClient client;

        public HomeFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);

            todayRecView = view.findViewById(R.id.todayRecView);
            propertyRecView = view.findViewById(R.id.propertyRecView);
            electronicsRecView = view.findViewById(R.id.electronicsRecView);
            bookRecView = view.findViewById(R.id.bookRecView);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            todayRecView.setLayoutManager(layoutManager);



            productList = new ArrayList<>();
            logementProductList  = new ArrayList<>();
            electronicsList = new ArrayList<>();
            booksList = new ArrayList<>();
            
            adapterToday = new ProductAdapter(getActivity(), productList);
            adapterProperty = new ProductAdapter(getActivity(), logementProductList);
            adapterElectronics = new ProductAdapter(getActivity(), electronicsList);
            adapterBooks = new ProductAdapter(getActivity(), booksList);
            
            todayRecView.setAdapter(adapterToday);
            propertyRecView.setAdapter(adapterProperty);
            electronicsRecView.setAdapter(adapterElectronics);
            bookRecView.setAdapter(adapterBooks);

            TextView all = view.findViewById(R.id.today_GroupViewAll);
            all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DisplayProductList.class);
                    intent.putExtra("category", "");
                    startActivity(intent);
                }
            });

            TextView books = view.findViewById(R.id.books_ViewAll);
            books.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DisplayProductList.class);
                    intent.putExtra("category", "Books");
                    startActivity(intent);
                }
            });

            TextView electronics = view.findViewById(R.id.electronics_ViewAll);
            electronics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DisplayProductList.class);
                    intent.putExtra("category", "Electronics");
                    startActivity(intent);
                }
            });

            TextView rentals = view.findViewById(R.id.property_ViewAll);
            rentals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DisplayProductList.class);
                    intent.putExtra("category", "Rentals");
                    startActivity(intent);
                }
            });
            client = new OkHttpClient();

            fetchProducts();

            return view;
        }

        private void fetchProducts() {
            String url = "http://10.0.2.2:3000/products-single";

            Request request = new Request.Builder()
                    .url(url)
                    .build();

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
                            List<Product> productList = new ArrayList<>();
                            List<Product> logementProductList = new ArrayList<>();
                            List<Product> bookProductList = new ArrayList<>();
                            List<Product> electronicsProductList = new ArrayList<>();

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
                                if (category.equals("Rentals")) {
                                    logementProductList.add(product);
                                } else if (category.equals("Books")) {
                                    bookProductList.add(product);
                                } else if (category.equals("Electronics")) {
                                    electronicsProductList.add(product);
                                }

                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapterToday.setProductList(productList);
                                    adapterProperty.setProductList(logementProductList );
                                    adapterElectronics.setProductList(electronicsProductList);
                                    adapterBooks.setProductList(bookProductList);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("HTTP_REQUEST", "Failed to parse products JSON: " + e.getMessage());
                        }
                    } else {
                        //throw new IOException("Unexpected code " + response);
                        Log.e("HTTP_REQUEST", "Failed to fetch products: " + response.code() + " - " + response.message());
                    }
                }
            });
        }

    }

