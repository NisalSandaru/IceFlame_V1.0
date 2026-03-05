package com.nisal.iceflame.network;

import com.nisal.iceflame.model.ProductDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductApi {

    @GET("api/v1.0/products/category/{categoryId}")
    Call<List<ProductDto>> getProductsByCategory(@Path("categoryId") Long categoryId);

    @GET("api/v1.0/products/{productId}")
    Call<ProductDto> getProductById(@Path("productId") Long productId);
}
