package com.example.studentmarketplace;

import static android.content.ContentValues.TAG;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import com.example.studentmarketplace.*;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmarketplace.ApiClientCallback;
import com.example.studentmarketplace.R;
import com.example.studentmarketplace.activity.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import android.Manifest;


public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private List<String> mImageUrls = new ArrayList<>();
    private List<File> mImageFiles = new ArrayList<>();
    private ImageAdapter mAdapter;


    private static final String CATEGORIES_URL = "http://10.0.2.2:3000/categories/";

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CATEGORIES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private Spinner categorySpinner;
    private Spinner statusSpinner;

    String url = "http://10.0.2.2:3000/products";


    private EditText etTitle, etDescription, etPrice;
    private Spinner etCategory, etStatus;

    private FusedLocationProviderClient fusedLocationClient;
    private Button btnAddItem;

    private double latitude, longitude;

    private static final int PERMISSION_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellitem_activity);


        statusSpinner = findViewById(R.id.status_spinner);
        categorySpinner = findViewById(R.id.category_spinner);

        loadCategories();
        loadStatus();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        etTitle = findViewById(R.id.title_edit_text);
        etDescription = findViewById(R.id.description_edit_text);
        etPrice = findViewById(R.id.price_edit_text);
        etCategory = findViewById(R.id.category_spinner);
        etStatus = findViewById(R.id.status_spinner);
        btnAddItem = findViewById(R.id.add_item_button);


        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addItem();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        long timestamp = System.currentTimeMillis();
                        File file = new File(getCacheDir(), "image" + timestamp + ".jpg");
                        FileOutputStream outputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[4 * 1024];
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, read);
                        }
                        outputStream.flush();
                        outputStream.close();
                        mImageFiles.add(file);
                        mImageUrls.add(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    long timestamp = System.currentTimeMillis();
                    File file = new File(getCacheDir(), "image" + timestamp + ".jpg");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[4 * 1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    outputStream.flush();
                    outputStream.close();
                    mImageFiles.add(file);
                    mImageUrls.add(file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void addItem() throws JSONException {


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("id", null);


        double latitude = 47.620867;
        double longitude = -65.674760;

        Activity mActivity = AddItemActivity.this;

        AddProductTask addProductTask = new AddProductTask(AddItemActivity.this, mActivity, etTitle, etDescription, etPrice, etCategory, btnAddItem, etStatus, sharedPreferences, latitude, longitude, mImageFiles);
        addProductTask.execute();

        mImageUrls.clear();
        //mImageFiles.clear();
        mAdapter.notifyDataSetChanged();


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

    private void loadStatus() {
        // Create instance of ApiService using Retrofit
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        // Call the API
        Call<List<String>> call = apiInterface.getStatus();
        call.enqueue(new retrofit2.Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                if (response.isSuccessful()) {
                    // Update spinner with categories data
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddItemActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, response.body());
                    statusSpinner.setAdapter(adapter);
                } else {
                    // Handle error
                    String errorMessage = "Error " + response.code() + ": " + response.message();
                    Toast.makeText(AddItemActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "Failed to load status: " + t.getMessage());
            }
        });
    }


}
