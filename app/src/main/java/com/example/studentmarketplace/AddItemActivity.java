package com.example.studentmarketplace;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AddItemActivity extends AppCompatActivity {
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mPriceEditText;
    private Button mAddItemButton;
    private Spinner mCategorySpinner;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellitem_activity);

        mTitleEditText = findViewById(R.id.edit_text_title);
        mDescriptionEditText = findViewById(R.id.edit_text_description);
        mPriceEditText = findViewById(R.id.edit_text_price);
        mAddItemButton = findViewById(R.id.button_add_item);
        mCategorySpinner = findViewById(R.id.spinner_category);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("items");

        mAddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    private void addItem() {
        String title = mTitleEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String category = mCategorySpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(title)) {
            mTitleEditText.setError("Title is required");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            mDescriptionEditText.setError("Description is required");
            return;
        }

        if (TextUtils.isEmpty(priceString)) {
            mPriceEditText.setError("Price is required");
            return;
        }

        double price = Double.parseDouble(priceString);

        String userId = mCurrentUser.getUid();
        String itemId = mDatabase.push().getKey();

        Item item = new Item(itemId, title, description, price, category, userId);

        // Check if permission to access location has been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Get the location manager
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Get the last known location
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Save the latitude and longitude to a variable
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
        } else {
            // Permission has not been granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }


        mDatabase.child(itemId).setValue(item).addOnCompleteListener(new ßOnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddItemActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

