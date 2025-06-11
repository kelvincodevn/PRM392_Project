package com.example.pcbuilderguideapp.network;

import com.example.pcbuilderguideapp.models.Product;
import com.example.pcbuilderguideapp.models.CartItemRequest;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Body;

public interface ApiService {
    @GET("Product/{id}")
    Call<Product> getProduct(@Path("id") int productId);

    @POST("Cart/items")
    Call<Void> addToCart(@Body CartItemRequest request);
} 