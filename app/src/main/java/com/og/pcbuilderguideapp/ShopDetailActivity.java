package com.og.pcbuilderguideapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.og.pcbuilderguideapp.models.Product;
import com.og.pcbuilderguideapp.network.RetrofitClient;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class ShopDetailActivity extends AppCompatActivity {
    private TextView tvProductName, tvCompanyName, tvPrice, tvStock, tvDescription, tvQuantity;
    private ImageView ivProductImage, ivBack, ivFavorite;
    private Button btnAddCart, btnDecreaseQuantity, btnIncreaseQuantity;
    private int productId;
    private int selectedQuantity = 1;
    private int maxQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        // Initialize views
        initializeViews();
        
        // Get product ID from intent
        productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load product details
        loadProductDetails();

        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        tvProductName = findViewById(R.id.tvProductDetailName);
        tvCompanyName = findViewById(R.id.tvProductDetailCompanyName);
        tvPrice = findViewById(R.id.tvProductDetailPrice);
        tvStock = findViewById(R.id.tvProductDetailStock);
        tvDescription = findViewById(R.id.tvProductDetailDescription);
        tvQuantity = findViewById(R.id.tvQuantity);
        ivProductImage = findViewById(R.id.ivProductDetailImage);
        ivBack = findViewById(R.id.ivBack);
        ivFavorite = findViewById(R.id.ivFavorite);
        btnAddCart = findViewById(R.id.btnAddCart);
        btnDecreaseQuantity = findViewById(R.id.btnDecreaseQuantity);
        btnIncreaseQuantity = findViewById(R.id.btnIncreaseQuantity);
    }

    private void loadProductDetails() {
        RetrofitClient.getInstance(this)
                .getApiService()
                .getProduct(productId)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Product product = response.body();
                            // Log product details to check received data
                            Log.d("ShopDetailActivity", "Product received: " + product.getName());
                            Log.d("ShopDetailActivity", "Company: " + product.getCompanyName());
                            Log.d("ShopDetailActivity", "Stock: " + product.getQuantity());
                            Log.d("ShopDetailActivity", "Price: " + product.getPrice());
                            Log.d("ShopDetailActivity", "Description: " + product.getDescription());
                            Log.d("ShopDetailActivity", "Image URL: " + product.getImageUrl());

                            displayProductDetails(product);
                        } else {
                            // Add this log for debugging
                            try {
                                Log.e("ShopDetailActivity", "Raw response: " + (response.errorBody() != null ? response.errorBody().string() : "null"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(ShopDetailActivity.this, 
                                "Failed to load product details", 
                                Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(ShopDetailActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayProductDetails(Product product) {
        tvProductName.setText(product.getName());
        tvCompanyName.setText("Company: " + product.getCompanyName());
        tvPrice.setText(String.format("%,.2f VND", product.getPrice()));
        tvStock.setText("Stock: " + product.getQuantity());
        tvDescription.setText(product.getDescription());
        maxQuantity = product.getQuantity();

        // Load product image using Firebase Storage
        String imageUrl = product.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_gpu_sample)
                    .error(R.drawable.ic_gpu_sample)
                    .into(ivProductImage);
            } else {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imageUrl);
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get()
                        .load(uri.toString())
                        .placeholder(R.drawable.ic_gpu_sample)
                        .error(R.drawable.ic_gpu_sample)
                        .into(ivProductImage);
                }).addOnFailureListener(exception -> {
                    ivProductImage.setImageResource(R.drawable.ic_gpu_sample);
                });
            }
        } else {
            ivProductImage.setImageResource(R.drawable.ic_gpu_sample);
        }
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivFavorite.setOnClickListener(v -> {
            // TODO: Implement favorite functionality
            Toast.makeText(this, "Favorite clicked", Toast.LENGTH_SHORT).show();
        });

        btnAddCart.setOnClickListener(v -> addToCart());

        btnDecreaseQuantity.setOnClickListener(v -> {
            if (selectedQuantity > 1) {
                selectedQuantity--;
                updateQuantityDisplay();
            }
        });

        btnIncreaseQuantity.setOnClickListener(v -> {
            if (selectedQuantity < maxQuantity) {
                selectedQuantity++;
                updateQuantityDisplay();
            } else {
                Toast.makeText(this, "Maximum quantity reached", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateQuantityDisplay() {
        tvQuantity.setText(String.valueOf(selectedQuantity));
    }

    private void addToCart() {
        if (selectedQuantity <= 0) {
            Toast.makeText(this, "Please select a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedQuantity > maxQuantity) {
            Toast.makeText(this, "Quantity exceeds available stock", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ShopDetailActivity", "Selected quantity: " + selectedQuantity);
        Log.d("ShopDetailActivity", "Product ID: " + productId);

        RetrofitClient.getInstance(this)
                .getApiService()
                .addToCart(productId, selectedQuantity)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ShopDetailActivity.this,
                                "Added to cart successfully",
                                Toast.LENGTH_SHORT).show();
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.e("ShopDetailActivity", "Failed to add to cart. Error code: " + response.code() + ", Error body: " + errorBody);
                            Toast.makeText(ShopDetailActivity.this,
                                "Failed to add to cart: " + errorBody,
                                Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("ShopDetailActivity", "Error adding to cart: " + t.getMessage(), t);
                        Toast.makeText(ShopDetailActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    }
                });
    }
} 