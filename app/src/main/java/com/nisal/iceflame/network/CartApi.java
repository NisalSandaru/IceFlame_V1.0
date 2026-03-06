package com.nisal.iceflame.network;

import com.nisal.iceflame.model.AddToCartRequest;
import com.nisal.iceflame.model.CartDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface CartApi {

    // ADD TO CART
    @POST("api/v1.0/cart/add")
    Call<CartDto> addToCart(
            @Query("userId") Long userId,
            @Body AddToCartRequest request
            );

    // GET USER CART
    @GET("api/cart/{userId}")
    Call<CartDto> getCart(
            @Path("userId") Long userId
    );

    // UPDATE QUANTITY
    @PUT("api/cart/update")
    Call<CartDto> updateQuantity(
            @Query("cartItemId") Long cartItemId,
            @Query("quantity") int quantity
    );

    // REMOVE ITEM
    @DELETE("api/cart/remove/{cartItemId}")
    Call<CartDto> removeItem(
            @Path("cartItemId") Long cartItemId
    );

    // CLEAR CART
    @DELETE("api/cart/clear/{userId}")
    Call<String> clearCart(
            @Path("userId") Long userId
    );
}
