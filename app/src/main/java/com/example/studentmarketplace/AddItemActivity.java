package com.example.studentmarketplace;

import static android.content.ContentValues.TAG;

import  com.example.studentmarketplace.*;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import retrofit2.Call;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmarketplace.ApiClientCallback;
import com.example.studentmarketplace.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private List<String> mImageUrls = new ArrayList<>();
    private ImageAdapter mAdapter;




    private static final String CATEGORIES_URL = "http://10.0.2.2:3000/categories/";

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CATEGORIES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private Spinner categorySpinner;

    String url = "http://10.0.2.2:3000/products";


    private EditText etTitle, etDescription, etPrice;
    private Spinner etCategory;

    private FusedLocationProviderClient fusedLocationClient;
    private Button btnAddItem;

    private static final int PERMISSION_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellitem_activity);

        categorySpinner = findViewById(R.id.category_spinner);

        loadCategories();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etTitle = findViewById(R.id.title_edit_text);
        etDescription = findViewById(R.id.description_edit_text);
        etPrice = findViewById(R.id.price_edit_text);
        etCategory = findViewById(R.id.category_spinner);

        btnAddItem = findViewById(R.id.add_item_button);


        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
        Button addPhotoButton = findViewById(R.id.add_photo_button);
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.photo_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ImageAdapter(this, mImageUrls);

        mRecyclerView.setAdapter(mAdapter);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    mImageUrls.add(imageUri.toString());
                    mAdapter.notifyDataSetChanged();
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                mImageUrls.add(imageUri.toString());
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void addItem() {

        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String price = etPrice.getText().toString();
        String category = etCategory.getSelectedItem().toString();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        SharedPreferences sharedPreferences = getSharedPreferences("my_app_preferences", MODE_PRIVATE);
                        String id = sharedPreferences.getString("id", null);
                        if (location != null) {
                            // User's location is retrieved successfully
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            try {
                                // Make a POST request to the server to retrieve user information
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url("http://10.0.2.2:3000/user_info")
                                        .addHeader("Authorization", "Bearer " + id)
                                        .get()
                                        .build();

                                Response response = client.newCall(request).execute();
                                String responseBody = response.body().string();

                                // Get the user ID from the server response
                                String userId = id ;

                                // Create the product JSON object
                                JSONObject product = new JSONObject();
                                product.put("name", title);
                                product.put("price", Double.parseDouble(price));
                                product.put("description", description);
                                product.put("userId", userId);
                                product.put("location", latitude + "," + longitude);
                                product.put("category", category);

                                // Add images to the request body
                                List<File> files = new ArrayList<>(); // replace with actual list of image files
                                MultipartBody.Builder builder = new MultipartBody.Builder();
                                builder.setType(MultipartBody.FORM);

                                for (File file : files) {
                                    builder.addFormDataPart("images", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                                }

                                // Add product data to the request body
                                builder.addFormDataPart("data", product.toString());

                                // Make a POST request to the server
                                Request request2 = new Request.Builder()
                                        .url(url)
                                        .post(builder.build())
                                        .build();

                                try (Response response2 = client.newCall(request).execute()) {
                                    if (response2.isSuccessful()) {
                                        responseBody = response2.body().string();
                                        Gson gson = new Gson();
                                        ApiClientCallback.ApiResponse apiResponse = gson.fromJson(responseBody, ApiClientCallback.ApiResponse.class);
                                        // Handle the successful response
                                    } else {
                                        String errorBody = response2.body().string();
                                        Gson gson = new Gson();
                                        ApiClientCallback.ApiError apiError = gson.fromJson(errorBody, ApiClientCallback.ApiError.class);
                                        // Handle the error response
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                // Handle the JSON error
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            // Handle the case where the location is null
                        }
                    }
                });
    }
    private void loadCategories() {
        // Create instance of ApiService using Retrofit
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        // Call the API
        Call<List<String>> call = apiInterface.getCategories();
        call.enqueue(new retrofit2.Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                if (response.isSuccessful()) {
                    // Update spinner with categories data
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddItemActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, response.body());
                    categorySpinner.setAdapter(adapter);
                } else {
                    // Handle error
                    String errorMessage = "Error " + response.code() + ": " + response.message();
                    Toast.makeText(AddItemActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "Failed to load categories: " + t.getMessage());
            }
        });
    }


}
