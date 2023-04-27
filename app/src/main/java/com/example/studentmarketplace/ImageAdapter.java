package com.example.studentmarketplace;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<String> mImageUrls;

    public ImageAdapter(Context context, List<String> imageUrls) {
        mContext = context;
        mImageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        String imageUrl = mImageUrls.get(position);
        Glide.with(mContext).load(imageUrl).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (e != null) {
                            e.logRootCauses("Image loading failed: " + imageUrl);
                        }
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imageView);
/*
        String imageUrl = mImageUrls.get(position);
        Log.d("adapter", "Image URLs: " + mImageUrls.toString());
        Picasso.get().setLoggingEnabled(true);
        Picasso.get()
                .load(imageUrl)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("ImageAdapter", "Image loaded successfully: " + imageUrl);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("ImageAdapter", "Image failed: " + imageUrl);
                    }
                });
*/
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public List<String> getImageUrls() {
        return mImageUrls;
    }

    public void clearImageUrls() {
        mImageUrls.clear();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}

