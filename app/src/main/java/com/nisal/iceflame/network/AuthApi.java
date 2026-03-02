package com.nisal.iceflame.network;

import com.nisal.iceflame.model.SignInRequest;
import com.nisal.iceflame.model.SignUpRequest;
import com.nisal.iceflame.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("api/v1.0/auth/signup")
    Call<Void> signUp(@Body SignUpRequest request);

    @POST("api/v1.0/auth/signin")
    Call<User> signIn(@Body SignInRequest request);
}