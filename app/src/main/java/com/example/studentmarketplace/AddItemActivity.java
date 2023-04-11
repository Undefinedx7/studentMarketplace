package com.example.studentmarketplace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.studentmarketplace.ApiClientCallback.ApiResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddItemActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etPrice;
    private Spinner etCategory;

    private FusedLocationProviderClient fusedLocationClient;
    private Button btnAddItem;

    private static final int PERMISSION_REQUEST_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellitem_activity);

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
    }

    private void addItem() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String price = etPrice.getText().toString();
        String category = etCategory.getSelectedItem().toString();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_LOCATION);
            return;

        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // User's location is retrieved successfully
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Call the API to add the item with the retrieved location
                            ApiClient apiClient = ApiClient.getInstance(AddItemActivity.this);
                            apiClient.addItem(title, description, Double.parseDouble(price), category, latitude, longitude, new ApiClientCallback.ApiResponseListener() {
                                @Override
                                public void onSuccess(ApiResponse response) {
                                    Toast.makeText(AddItemActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onError(ApiClientCallback.ApiError error) {
                                    Toast.makeText(AddItemActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

        if (title.isEmpty() || description.isEmpty() || price.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }



        ApiClient apiClient = ApiClient.getInstance(this);
        apiClient.addItem(title, description, Double.parseDouble(price), category, new ApiClientCallback.ApiResponseListener() {
            @Override
            public void onSuccess(ApiResponse response) {
                Toast.makeText(AddItemActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(ApiClientCallback.ApiError error) {
                Toast.makeText(AddItemActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
