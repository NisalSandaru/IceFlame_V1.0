package com.nisal.iceflame.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.8.100:8080/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AuthApi getAuthApi() {
        return getRetrofitInstance().create(AuthApi.class);
    }

    public static CategoryApi getCategoryApi() {
        return getRetrofitInstance().create(CategoryApi.class);
    }

    public static ProductApi getProductApi() {
        return getRetrofitInstance().create(ProductApi.class);
    }

    public static CartApi getCartApi() {
        return getRetrofitInstance().create(CartApi.class);
    }

    public static WishlistApi getWishlistApi() {
        return getRetrofitInstance().create(WishlistApi.class);
    }

    public static UserApi getUserApi() {
        return getRetrofitInstance().create(UserApi.class);
    }

    public static AddressApi getAddressApi() {
        return getRetrofitInstance().create(AddressApi.class);
    }

    public static OrderApi getOrderApi() {
        return getRetrofitInstance().create(OrderApi.class);
    }

    public static DeviceApi getDeviceApi() {
        return getRetrofitInstance().create(DeviceApi.class);
    }
}