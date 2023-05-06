package com.example.studentmarketplace;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SellerInfoFragment extends Fragment {
    private ImageView sellerProfilePicture;
    private TextView sellerName;
    private TextView sellerLocation;
    private Button contactSellerButton;

    private String imageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_seller_info, container, false);

        sellerProfilePicture = rootView.findViewById(R.id.seller_profile_picture);
        sellerName = rootView.findViewById(R.id.seller_name);
        sellerLocation = rootView.findViewById(R.id.seller_location);
        contactSellerButton = rootView.findViewById(R.id.contact_seller_button);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("seller_id", Context.MODE_PRIVATE);
        String sellerIdStr = String.valueOf(sharedPreferences.getInt("seller_id",0));
        int sellerId = Integer.parseInt(sellerIdStr);

        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("sellerId", sellerId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/seller_info")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        System.out.println(responseBody);

                        JSONObject data = json.getJSONObject("data");
                        String username = data.getString("username");
                        String createdAt = data.getString("created_at");

                        imageUrl = data.getString("image_urls");
                        imageUrl = imageUrl.replaceAll("\\\\", "");
                        imageUrl = imageUrl.replaceAll(" ", "");
                        imageUrl = imageUrl.replaceAll("\\\"]", "");
                        imageUrl = imageUrl.replaceAll("\"", "");
                        imageUrl = imageUrl.replaceAll("\\[", "");

                        ImageView imageView = rootView.findViewById(R.id.seller_profile_picture);
                        //String imageUrl = "http://10.0.2.2:8080/studentMarket/ressources/upload/profilePicture/test.jpg";


                        getActivity().runOnUiThread(() -> {
                            sellerName.setText(username);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            sdf.setTimeZone(android.icu.util.TimeZone.getTimeZone("UTC"));
                            try {
                                Picasso.get().load(imageUrl).into(imageView);
                                Date date = sdf.parse(createdAt);
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");
                                String year = outputFormat.format(date);
                                String joinedText = "Joined StudentMarketplace on " + year;
                                getActivity().runOnUiThread(() -> {
                                    sellerName.setText(username);
                                    sellerLocation.setText(joinedText);
                                });
                            } catch (ParseException | java.text.ParseException e) {
                                e.printStackTrace();
                            }

                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                    String error = response.body().string();
                    Log.d("SellerInfoFragment", "Error: " + error);
                }
            }
        });

        return rootView;
    }
}

