package com.example.studentmarketplace;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.Gson;

import org.json.JSONArray;
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


public class AddProductTask extends AsyncTask<Void, Void, ApiClientCallback.ApiResponse> {
    String url = "http://10.0.2.2:3000/products";
    private Context context;
    private EditText etTitle, etDescription, etPrice;
    private Spinner etCategory, etStatus;
    private Button btnAddItem;

    private Activity mActivity;



    private FusedLocationProviderClient fusedLocationClient;
    //private String userId;
    private double latitude;
    private double longitude;
    private SharedPreferences sharedPreferences;

    private List<File> mImageFiles;

    public AddProductTask(Context context, Activity activity,EditText pEtTitle, EditText pEtDescription, EditText pEtPrice, Spinner pEtCategory, Button pBtnAddItem, Spinner pEtStatus, SharedPreferences pSharedPreferences, double pLatitude, double pLongitude, List<File> pImageFiles) {
        this.context = context;
        this.mActivity = activity;
        this.etTitle = pEtTitle;
        this.etDescription = pEtDescription;
        this.etPrice = pEtPrice;
        this.etCategory = pEtCategory;
        this.btnAddItem = pBtnAddItem;
        this.etStatus = pEtStatus;
        this.sharedPreferences = pSharedPreferences;
        this.latitude = pLatitude;
        this.longitude = pLongitude;
        this.mImageFiles = pImageFiles;
    }


    @Override
    protected ApiClientCallback.ApiResponse doInBackground(Void... voids) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE);

        String userId = sharedPreferences.getString("id", null);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String price = etPrice.getText().toString();
        String category = etCategory.getSelectedItem().toString();
        String status = etStatus.getSelectedItem().toString();
        JSONArray imageUrlsJsonArray = new JSONArray();


        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", title)
                .addFormDataPart("price",price )
                .addFormDataPart("description", description)
                .addFormDataPart("category_name", category)
                .addFormDataPart("status_sell", status)
                .addFormDataPart("userId", String.valueOf(userId))
                .addFormDataPart("location", latitude + "," + longitude);

        // Add the image files to the builder
        for (File imageFile : mImageFiles) {
            builder.addFormDataPart("image", imageFile.getName(),
                    RequestBody.create(MediaType.parse("image/jpeg"), imageFile));
        }

        // Build the final request body from the builder
        RequestBody requestBody = builder.build();

        // Create the request with the final request body
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Gson gson = new Gson();
            ApiClientCallback.ApiResponse apiResponse = gson.fromJson(responseBody, ApiClientCallback.ApiResponse.class);
            if (apiResponse.isSuccess()) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Update UI here
                            Toast.makeText(context.getApplicationContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();

                            // Clear the input fields
                            etTitle.setText("");
                            etDescription.setText("");
                            etPrice.setText("");

                            mImageFiles.clear();

                            JSONObject responseJson = new JSONObject(responseBody);
                            String imageUrl = responseJson.getString("imageUrls");
                            imageUrlsJsonArray.put(imageUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });


            } else {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // There was an error adding the product
                        Toast.makeText(context.getApplicationContext(), "Error adding product: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}

    /*
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE);

        String userId = sharedPreferences.getString("id", null);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String price = etPrice.getText().toString();
        String category = etCategory.getSelectedItem().toString();
        String status = etStatus.getSelectedItem().toString();
        JSONArray imageUrlsJsonArray = new JSONArray();


        for (File imageFile : mImageFiles) {

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), imageFile))
                    .build();

        }


        JSONObject json = new JSONObject();
        try {
            json.put("name", title);
            json.put("description", description);
            if (!price.isEmpty()) {
                json.put("price", Double.parseDouble(price));
            }
            json.put("userId", userId);
            json.put("location", latitude + "," + longitude);
            json.put("category_name", category);
            json.put("status_sell", status);

            //json.put("image_urls", imageUrlsJsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
            RequestBody body = RequestBody.create(mediaType, json.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Gson gson = new Gson();
            ApiClientCallback.ApiResponse apiResponse = gson.fromJson(responseBody, ApiClientCallback.ApiResponse.class);
            if (apiResponse.isSuccess()) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Update UI here
                            Toast.makeText(context.getApplicationContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();

                            // Clear the input fields
                            etTitle.setText("");
                            etDescription.setText("");
                            etPrice.setText("");

                            mImageFiles.clear();

                            JSONObject responseJson = new JSONObject(responseBody);
                            String imageUrl = responseJson.getString("imageUrls");
                            imageUrlsJsonArray.put(imageUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });


            } else {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // There was an error adding the product
                        Toast.makeText(context.getApplicationContext(), "Error adding product: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
        */


 /* JSONObject json = new JSONObject();
            try {
                json.put("name", title);
                json.put("description", description);
                if (!price.isEmpty()) {
                    json.put("price", Double.parseDouble(price));
                }
                json.put("userId", userId);
                json.put("location", latitude + "," + longitude);
                json.put("category_name", category);
                json.put("status_sell", status);

                //json.put("image_urls", imageUrlsJsonArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }
*/
/*
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                ApiClientCallback.ApiResponse apiResponse = gson.fromJson(responseBody, ApiClientCallback.ApiResponse.class);
                if (apiResponse.isSuccess()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update UI here
                            Toast.makeText(context.getApplicationContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();
                            String imageUrl = responseJson.getString("url");

                            imageUrlsJsonArray.put(imageUrl);
                            // Clear the input fields
                            etTitle.setText("");
                            etDescription.setText("");
                            etPrice.setText("");

                            mImageFiles.clear();

                        }

                    });


                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // There was an error adding the product
                            Toast.makeText(context.getApplicationContext(), "Error adding product: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

    */

