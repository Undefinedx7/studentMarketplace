package com.example.studentmarketplace;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmarketplace.com.example.studentmarketplace.ItemAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Item item;
    private ImageView itemImage;
    private TextView itemTitle;
    private TextView itemPrice;
    private TextView itemDescription;
    private TextView itemLocation;
    private MapView mapView;
    private RecyclerView similarItemsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_activity);

        // Get item information
        item = new Item("Sample Item", "This is a sample item description.", R.drawable.sample_item_image, 49.99, 37.7749, -122.4194);

        // Find views
        itemImage = findViewById(R.id.item_image);
        itemTitle = findViewById(R.id.item_title);
        itemPrice = findViewById(R.id.item_price);
        itemDescription = findViewById(R.id.item_description);
        itemLocation = findViewById(R.id.item_location);
        mapView = findViewById(R.id.map_view);
        similarItemsRecyclerView = findViewById(R.id.similar_items_recycler_view);

        // Set item information
        itemImage.setImageResource(item.getImageResourceId());
        itemTitle.setText(item.getName());
        itemPrice.setText(String.format("$%.2f", item.getPrice()));
        itemDescription.setText(item.getDescription());
        itemLocation.setText(String.format("Location: (%.4f, %.4f)", item.getLatitude(), item.getLongitude()));

        // Initialize map view
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Set up similar items recycler view
        List<Item> similarItems = new ArrayList<>();
        similarItems.add(new Item("Similar Item 1", "This is a similar item description.", R.drawable.sample_item_image, 39.99, 37.7749, -122.4194));
        similarItems.add(new Item("Similar Item 2", "This is another similar item description.", R.drawable.sample_item_image, 59.99, 37.7749, -122.4194));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        similarItemsRecyclerView.setLayoutManager(layoutManager);
        ItemAdapter adapter = new ItemAdapter(similarItems);
        similarItemsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker for the item's location and move the camera to the marker
        LatLng location = new LatLng(item.getLatitude(), item.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(location).title(item.getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
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

