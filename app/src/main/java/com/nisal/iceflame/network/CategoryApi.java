package com.nisal.iceflame.network;

import com.nisal.iceflame.model.CategoryDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoryApi {

    @GET("api/v1.0/categories")
    Call<List<CategoryDto>> getCategories();

    @GET("api/v1.0/categories/{id}")
    Call<CategoryDto> getCategoryById(@Path("id") Long id);
}
