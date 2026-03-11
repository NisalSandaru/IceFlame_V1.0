package com.nisal.iceflame.network;

import com.nisal.iceflame.model.CartDto;
import com.nisal.iceflame.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {
    @GET("api/v1.0/users/{userId}")
    Call<User> getUserById(
            @Path("userId") Long userId
    );

    @PUT("api/v1.0/users/{id}")
    Call<User> updateUser(
            @Path("id") Long id,
            @Body User user
    );
}
