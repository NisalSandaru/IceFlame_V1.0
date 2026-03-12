package com.nisal.iceflame.network;

import com.nisal.iceflame.model.AddressDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddressApi {
    @POST("api/v1.0/addresses")
    Call<AddressDto> addAddress(@Body AddressDto address);

    @GET("api/v1.0/addresses/user/{userId}")
    Call<List<AddressDto>> getUserAddresses(@Path("userId") Long userId);

    @PUT("api/v1.0/addresses/{id}")
    Call<AddressDto> updateAddress(
            @Path("id") Long id,
            @Body AddressDto address
    );

    @DELETE("api/v1.0/addresses/{id}")
    Call<ResponseBody> deleteAddress(@Path("id") Long id);
}
