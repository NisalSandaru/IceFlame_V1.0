package com.nisal.iceflame.network;

import com.nisal.iceflame.model.CheckoutRequest;
import com.nisal.iceflame.model.OrderDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApi {
    @POST("api/v1.0/orders/create")
    Call<OrderDto> createOrder(@Body CheckoutRequest request);

    @POST("api/v1.0/orders/confirm-payment/{orderId}")
    Call<OrderDto> confirmPayment(@Path("orderId") Long orderId);
}
