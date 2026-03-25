package com.nisal.iceflame.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import com.google.gson.JsonObject;

public interface CloudinaryApi {

    @Multipart
    @POST("https://api.cloudinary.com/v1_1/drbhpx730/image/upload")
    Call<JsonObject> uploadImage(
            @Part MultipartBody.Part file,
            @Part("upload_preset") RequestBody uploadPreset
    );
}