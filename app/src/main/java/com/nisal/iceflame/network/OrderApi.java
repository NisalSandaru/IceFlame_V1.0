package com.nisal.iceflame.network;

import com.nisal.iceflame.model.CheckoutRequest;
import com.nisal.iceflame.model.OrderDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApi {
    @POST("api/v1.0/orders/create")
    Call<OrderDto> createOrder(@Body CheckoutRequest request);

    @POST("api/v1.0/orders/confirm-payment/{orderId}")
    Call<OrderDto> confirmPayment(@Path("orderId") Long orderId);

    @GET("/api/v1.0/orders/user/{userId}/current")
    Call<List<OrderDto>> getCurrentOrders(@Path("userId") Long userId);

    @GET("/api/v1.0/orders/user/{userId}/previous")
    Call<List<OrderDto>> getPreviousOrders(@Path("userId") Long userId);
}
