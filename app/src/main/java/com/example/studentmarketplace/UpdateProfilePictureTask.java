package com.example.studentmarketplace;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.app.Activity;
import android.content.Context;

public class UpdateProfilePictureTask extends AsyncTask<Void, Void, ApiClientCallback.ApiResponse> {

    private Context mContext;
    private ImageView profileImageView;
    private File mImageFile;

    public UpdateProfilePictureTask(Context context, File imageFile) {
        mContext = context;
        mImageFile = imageFile;
    }

    protected ApiClientCallback.ApiResponse doInBackground(Void... voids) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE);

        String userId = sharedPreferences.getString("id", null);
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("imageUrls", mImageFile.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), mImageFile))
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/update_profilePic/" + userId)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Gson gson = new Gson();
            System.out.println(responseBody);
            ApiClientCallback.ApiResponse apiResponse = gson.fromJson(responseBody, ApiClientCallback.ApiResponse.class);
            return apiResponse;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPostExecute(ApiClientCallback.ApiResponse apiResponse) {
        super.onPostExecute(apiResponse);
        if (apiResponse != null && apiResponse.isSuccess()) {
            Toast.makeText(mContext, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show();
            ImageView imageView = ((Activity) mContext).findViewById(R.id.profilePicture);
            Picasso.get()
                    .load(mImageFile)
                    .into(imageView);
        } else {
            Toast.makeText(mContext, "Error updating profile picture: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}

    /*
    @Override
    protected ApiClientCallback.ApiResponse doInBackground(Void... voids) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("my_app_preferences", Context.MODE_PRIVATE);

        String userId = sharedPreferences.getString("id", null);
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", userId)
                .addFormDataPart("imageUrls", mImageFile.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), mImageFile))
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/update_profilePic")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Gson gson = new Gson();
            System.out.println(responseBody);
            ApiClientCallback.ApiResponse apiResponse = gson.fromJson(responseBody, ApiClientCallback.ApiResponse.class);
            return apiResponse;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
*/




/*
public class UpdateProfilePictureTask extends AsyncTask<Uri, Void, Boolean> {

    private Context mContext;

    private String mUserId;
    private ApiClientCallback mCallback;

    public UpdateProfilePictureTask(Context context, String userId, ApiClientCallback callback) {
        mContext = context;
        mUserId = userId;
        mCallback = callback;
    }
    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if (result) {
            //Toast.makeText(context.getApplicationContext(), "Error adding product: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            //mCallback.onFailure();
        }
    }


    @Override
    protected Boolean doInBackground(Uri... uris) {

        if (uris.length == 0 || uris[0] == null) {
            return false;
        }

        Uri selectedImageUri = uris[0];

        try {
            InputStream inputStream = mContext.getContentResolver().openInputStream(selectedImageUri);
            byte[] imageData = getBytes(inputStream);
            inputStream.close();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("sellerId", String.valueOf(mUserId))

                    .build();


            Request request = new Request.Builder()
                    .url("http://10.0.2.2:3000/update_profilePic")
                    .put(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}

*/
/*
public class UpdateProfilePictureTask extends AsyncTask<File, Void, String> {

    private Context context;
    private ImageView mImageView;

    public UpdateProfilePictureTask(Context context, ImageView imageView) {
        this.context = context;
        mImageView = imageView;
    }

    @Override
    protected String doInBackground(File... files) {
        OkHttpClient client = new OkHttpClient();

            SharedPreferences sharedPreferences = context.getSharedPreferences("my_app_preferences", MODE_PRIVATE);
            String userId = sharedPreferences.getString("id", null);


        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("sellerId", userId)
                .addFormDataPart("image", files[0].getName(),
                        RequestBody.create(mediaType, files[0]))
                .build();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/update_profile_picture")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                String status = json.getString("status");
                if (status.equals("success")) {
                    String imageUrl = json.getString("imageUrl");
                    return imageUrl;
                } else {
                    String message = json.getString("message");
                    throw new RuntimeException("Failed to update profile picture: " + message);
                }
            } else {
                throw new RuntimeException("Failed to update profile picture: " + response.message());
            }
        } catch (IOException | JSONException e) {
            throw new RuntimeException("Failed to update profile picture: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String imageUrl) {
        // Update the ImageView with the new image URL
        Picasso.get()
                .load(imageUrl)
                .into(mImageView);
    }
}*/

