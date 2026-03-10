package com.nisal.iceflame.network;

import com.nisal.iceflame.model.WishlistDto;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WishlistApi {
    @POST("api/v1.0/wishlist/add")
    Call<WishlistDto> addToWishlist(
            @Query("userId") Long userId,
            @Query("productId") Long productId
    );

    // GET USER CART
    @GET("api/v1.0/wishlist/{userId}")
    Call<WishlistDto> getWishlist(
            @Path("userId") Long userId
    );

    @DELETE("api/v1.0/wishlist/remove")
    Call<WishlistDto> removeWishItem(
            @Query("userId") Long userId,
            @Query("productId") Long productId
    );

    // CLEAR CART
    @DELETE("api/v1.0/wishlist/clear/{userId}")
    Call<String> clearWishlist(
            @Path("userId") Long userId
    );
}
