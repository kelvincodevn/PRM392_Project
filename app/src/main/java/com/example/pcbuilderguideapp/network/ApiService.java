package com.example.pcbuilderguideapp.network;

import com.example.pcbuilderguideapp.models.Product;
import com.example.pcbuilderguideapp.models.CartItemRequest;
import com.example.pcbuilderguideapp.models.Cart;
import com.example.pcbuilderguideapp.models.Order;
import com.example.pcbuilderguideapp.models.OrderStatusRequest;
import com.example.pcbuilderguideapp.models.OrderResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.Query;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.PATCH;
import java.util.List;

public interface ApiService {
    @GET("Product/{id}")
    Call<Product> getProduct(@Path("id") int productId);

    @POST("Cart/items")
    Call<Void> addToCart(@Query("productId") int productId, @Query("quantity") int quantity);

    @GET("Cart")
    Call<Cart> getCart();

    @PUT("Cart/items/{cartItemId}")
    Call<Void> updateCartItem(@Path("cartItemId") int cartItemId, @Query("quantity") int quantity);

    @DELETE("Cart/items/{id}")
    Call<Void> deleteCartItem(@Path("id") int id);

    @GET("Order/my-orders")
    Call<OrderResponse> getMyOrders();

    @PATCH("Order/{id}/status")
    Call<Void> updateOrderStatus(@Path("id") int orderId, @Body OrderStatusRequest statusRequest);
} 