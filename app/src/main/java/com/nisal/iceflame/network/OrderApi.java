package com.nisal.iceflame.network;

import com.nisal.iceflame.model.CheckoutRequest;
import com.nisal.iceflame.model.OrderDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrderApi {
    @POST("api/v1.0/orders/checkout")
    Call<OrderDto> checkout(@Body CheckoutRequest request);
}
