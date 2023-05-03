package com.example.studentmarketplace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.studentmarketplace.ItemAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItemDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView itemImage;
    private TextView itemTitle;
    private TextView itemPrice, itemCategorie;
    private TextView itemDescription;
    private TextView itemLocation;
    private MapView mapView;
    private RecyclerView similarItemsRecyclerView;
    private int sellerId;

    private ImageAdapter mAdapter;

    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_activity);
/*
        RecyclerView itemImagesRecyclerView = findViewById(R.id.item_images);
        itemImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

*/

        itemCategorie = findViewById(R.id.item_categorie);
        itemImage = findViewById(R.id.item_image);
        itemTitle = findViewById(R.id.item_title);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        itemLocation = findViewById(R.id.item_location);
        mapView = findViewById(R.id.map_view);
        similarItemsRecyclerView = findViewById(R.id.similar_items_recycler_view);

        new LoadProductDetailsTask().execute();

    }
    private class LoadProductDetailsTask extends AsyncTask<Void, Void, JSONObject> {
        int productId = Integer.parseInt(getIntent().getStringExtra("productId")); //735860161;


        @Override
        protected JSONObject doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient();

            JSONObject json = new JSONObject();
            try {
                json.put("productId", productId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:3000/product-details")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                System.out.println(responseBody);
                JSONObject responseJson = new JSONObject(responseBody);
                JSONObject productData = responseJson.getJSONObject("data");
                Log.d("Product Data", productData.toString());

                return productData;
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        protected void onPostExecute(JSONObject productData) {

            itemTitle.setText(productData.optString("name"));
            itemCategorie.setText(productData.optString("category"));
            itemPrice.setText(String.format("$%.2f", productData.optDouble("price")));
            itemDescription.setText(productData.optString("description"));

            String imagesString = productData.optString("images");
            JSONArray imagesJsonArray = null;
            try {
                imagesJsonArray = new JSONArray(imagesString);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            List<String> imageUrls = new ArrayList<>();
            imageUrls.clear();
            for (int i = 0; i < imagesJsonArray.length(); i++) {
                String imageUrl = imagesJsonArray.optString(i);
                imageUrl = imageUrl.replaceAll("\\\\", "");
                imageUrl = imageUrl.replaceAll(" ", "");
                imageUrl = imageUrl.replaceAll("\\\"]", "");
                imageUrl = imageUrl.replaceAll("\"", "");
                imageUrls.add(imageUrl);
            }


/*
            ImageView imageView = findViewById(R.id.image_view);
            String imageUrl = "http://10.0.2.2:8080/studentMarket/ressources/upload/test.jpg";
            Picasso.get().load(imageUrl).into(imageView);


*/
            RecyclerView mRecyclerView = findViewById(R.id.photo_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(ItemDetailsActivity.this));

            mAdapter = new ImageAdapter(ItemDetailsActivity.this, imageUrls);

            Log.d("MainActivity", "Image URLs: " + imageUrls.toString());
            mRecyclerView.setAdapter(mAdapter);


            try {
                sellerId = productData.getInt("sellerId");
                System.out.println(sellerId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            SharedPreferences sharedPreferences = getSharedPreferences("seller_id", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("seller_id", sellerId);
            editor.apply();


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, new SellerInfoFragment());
            fragmentTransaction.commit();

            String location = null;
            try {
                location = productData.getString("location");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String[] latLng = location.split(",");
            latitude = Double.parseDouble(latLng[0]);
            longitude = Double.parseDouble(latLng[1]);



            mapView.onCreate(null);
            mapView.getMapAsync(ItemDetailsActivity.this);

            mAdapter.notifyDataSetChanged();

            List<Item> similarItems = new ArrayList<>();
            similarItems.add(new Item("Similar Item 1", "This is a similar item description.", R.drawable.sample_item_image, 39.99, 37.7749, -122.4194));
            similarItems.add(new Item("Similar Item 2", "This is another similar item description.", R.drawable.sample_item_image, 59.99, 37.7749, -122.4194));
            LinearLayoutManager layoutManager = new LinearLayoutManager(ItemDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
            similarItemsRecyclerView.setLayoutManager(layoutManager);
            //ItemAdapter adapter = new ItemAdapter(similarItems);
            //similarItemsRecyclerView.setAdapter(adapter);
        }



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng position = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(position));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        mAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}


/*


        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("productId", productId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/products-details")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JSONObject responseJson = new JSONObject(responseBody);
            JSONObject productData = responseJson.getJSONObject("data");

            itemTitle.setText(productData.getString("name"));
            itemPrice.setText(String.format("$%.2f", productData.getDouble("price")));
            itemDescription.setText(productData.getString("description"));
            itemLocation.setText(String.format("Location: (%.4f, %.4f)", productData.getDouble("latitude"), productData.getDouble("longitude")));

            // Initialize map view
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);

            List<Item> similarItems = new ArrayList<>();
            similarItems.add(new Item("Similar Item 1", "This is a similar item description.", R.drawable.sample_item_image, 39.99, 37.7749, -122.4194));
            similarItems.add(new Item("Similar Item 2", "This is another similar item description.", R.drawable.sample_item_image, 59.99, 37.7749, -122.4194));
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            similarItemsRecyclerView.setLayoutManager(layoutManager);
            ItemAdapter adapter = new ItemAdapter(similarItems);
            similarItemsRecyclerView.setAdapter(adapter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


* */

