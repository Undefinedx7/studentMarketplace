package com.example.studentmarketplace.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.studentmarketplace.*;
import com.example.studentmarketplace.UserInfo;
import com.example.studentmarketplace.activity.LoginActivity;
import com.example.studentmarketplace.activity.MainActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ProfileFragment extends Fragment {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;

    private Button logoutButton;

    private TextView timeTextView;

    private String imageUrlsString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        logoutButton = view.findViewById(R.id.logoutButton);
        timeTextView = view.findViewById(R.id.time);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("my_app_preferences", MODE_PRIVATE);
        String userId = sharedPreferences.getString("id", null);


        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("sellerId", userId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

        okhttp3.Request request = new Request.Builder()
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
                        String name = data.getString("username");
                        String email = data.getString("email");
                        //String phone = data.getString("phone");
                        String createdAt = data.getString("created_at");
                        imageUrlsString = data.getString("image_urls");
                        imageUrlsString = imageUrlsString.replaceAll("\\\\", "");
                        imageUrlsString = imageUrlsString.replaceAll(" ", "");
                        imageUrlsString = imageUrlsString.replaceAll("\\\"]", "");
                        imageUrlsString = imageUrlsString.replaceAll("\"", "");
                        imageUrlsString = imageUrlsString.replaceAll("\\[", "");


                        Log.d("ProfileFragment", "First image URL: " + imageUrlsString);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateNameTextView(name);
                                updateEmailTextView(email);
                                //updatePhoneTextView(phone);
                                ImageView imageView = getView().findViewById(R.id.profilePicture);
                                String imageUrl = null;

                                /*
                                Glide.with(getActivity())
                                        .load(imageUrl)
                                        .into(imageView);
                                String imageUrl = "http://10.0.2.2:8080/studentMarket/ressources/upload/profilePicture/test.jpg";

                                 */
                                Date date = null;
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    sdf.setTimeZone(android.icu.util.TimeZone.getTimeZone("UTC"));
                                    date = sdf.parse(createdAt);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");
                                String year = outputFormat.format(date);
                                String joinedText = "Joined StudentMarketplace on " + year;
                                timeTextView.setText(joinedText);
                                Picasso.get()
                                        .load(imageUrlsString)
                                        .into(imageView);

                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove user ID from shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("id");
                editor.apply();

                // Redirect user to login screen
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void updateNameTextView(String name) {
        nameTextView.setText(name);
    }

    private void updateEmailTextView(String email) {
        emailTextView.setText(email);
    }

    private void updatePhoneTextView(String phone) {
        phoneTextView.setText(phone);
    }
}

        /*

        // Make API call to get user info
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Set the url for the GET request
        String url = "http://10.0.2.2:3000/seller_info";
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("sellerId", userId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Create a new GET request using JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the JSON response and update the UI
                        try {
                            String name = response.getString("name");
                            String email = response.getString("email");
                            String phone = response.getString("phone");

                            updateNameTextView(name);
                            updateEmailTextView(email);
                            updatePhoneTextView(phone);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(requireContext(), "Error fetching user info", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

        return view;

         */

/*
                        String firstImageUrl = imageUrlsString.getString(0);

                        firstImageUrl = firstImageUrl.replaceAll("\\\\", "");
                        firstImageUrl = firstImageUrl.replaceAll(" ", "");
                        firstImageUrl = firstImageUrl.replaceAll("\\\"]", "");
                        firstImageUrl = firstImageUrl.replaceAll("\"", "");
*/