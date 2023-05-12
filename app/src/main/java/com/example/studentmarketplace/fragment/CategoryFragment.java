package com.example.studentmarketplace.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.studentmarketplace.DisplayProductList;
import com.example.studentmarketplace.R;

public class CategoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        CardView furnitureCardView = view.findViewById(R.id.category_furniture);
        furnitureCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DisplayProductList.class);
                intent.putExtra("category", "Furniture");
                startActivity(intent);
            }
        });

        CardView rentalCardView = view.findViewById(R.id.category_rentals);
        rentalCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DisplayProductList.class);
                intent.putExtra("category", "Rentals");
                startActivity(intent);
            }
        });

        CardView electronicsCardView = view.findViewById(R.id.category_electronics);
        electronicsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DisplayProductList.class);
                intent.putExtra("category", "Electronics");
                startActivity(intent);
            }
        });

        return view;
    }
}

