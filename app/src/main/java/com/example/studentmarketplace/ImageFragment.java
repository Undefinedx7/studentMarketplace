package com.example.studentmarketplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studentmarketplace.R;
import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "ARG_IMAGE_URL";

    public static ImageFragment newInstance(String imageUrl) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = rootView.findViewById(R.id.image_view);
        String imageUrl = getArguments().getString(ARG_IMAGE_URL);
        Picasso.get().load(imageUrl).into(imageView);
        return rootView;
    }
}
