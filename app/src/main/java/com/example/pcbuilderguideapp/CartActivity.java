package com.example.pcbuilderguideapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.models.Cart;
import com.example.pcbuilderguideapp.models.CartItem;
import com.example.pcbuilderguideapp.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ImageView;
import android.content.Intent;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCartItems;
    private TextView tvTotalPrice;
    private Button btnProceedToPayment;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnProceedToPayment = findViewById(R.id.btnProceedToPayment);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        cartAdapter = new CartAdapter(cartItems);
        rvCartItems.setAdapter(cartAdapter);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        btnProceedToPayment.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.putExtra("cart_items", new java.util.ArrayList<>(cartItems));
            startActivity(intent);
        });

        fetchCart();
    }

    private void fetchCart() {
        RetrofitClient.getInstance(this)
            .getApiService()
            .getCart()
            .enqueue(new Callback<Cart>() {
                @Override
                public void onResponse(Call<Cart> call, Response<Cart> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        cartItems.clear();
                        List<CartItem> items = response.body().getCartItems();
                        if (items != null) {
                            cartItems.addAll(items);
                        }
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPrice();
                    } else {
                        Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Cart> call, Throwable t) {
                    Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.getProduct() != null) {
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }
        tvTotalPrice.setText("Total price $" + String.format("%.2f", total));
    }
} 