package com.nisal.iceflame.network;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DeviceApi {
    @POST("/api/v1.0/device")
    Call<Void> saveToken(
            @Query("userId") Long userId,
            @Query("token") String token
    );
}
