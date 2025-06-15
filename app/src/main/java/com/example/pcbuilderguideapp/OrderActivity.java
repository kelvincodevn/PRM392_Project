package com.example.pcbuilderguideapp;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.adapters.OrderAdapter;
import com.example.pcbuilderguideapp.models.Order;
import com.example.pcbuilderguideapp.models.OrderResponse;
import com.example.pcbuilderguideapp.models.OrderStatusRequest;
import com.example.pcbuilderguideapp.network.ApiService;
import com.example.pcbuilderguideapp.network.RetrofitClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {
    private RecyclerView rvOnDelivery;
    private RecyclerView rvHistory;
    private OrderAdapter onDeliveryAdapter;
    private OrderAdapter historyAdapter;
    private ApiService apiService;
    private LinearLayout layoutOnDelivery;
    private LinearLayout layoutHistory;
    private TextView tabOnDeliveryGradient;
    private TextView tabOnDeliveryPlain;
    private TextView tabHistoryGradient;
    private TextView tabHistoryPlain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize views
        rvOnDelivery = findViewById(R.id.rvOnDelivery);
        rvHistory = findViewById(R.id.rvHistory);
        tabOnDeliveryGradient = findViewById(R.id.tabOnDeliveryGradient);
        tabOnDeliveryPlain = findViewById(R.id.tabOnDeliveryPlain);
        tabHistoryGradient = findViewById(R.id.tabHistoryGradient);
        tabHistoryPlain = findViewById(R.id.tabHistoryPlain);

        // Initialize API service
        apiService = RetrofitClient.getInstance(this).getApiService();

        // Setup RecyclerViews
        rvOnDelivery.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        onDeliveryAdapter = new OrderAdapter(new ArrayList<>(), this);
        historyAdapter = new OrderAdapter(new ArrayList<>(), this);
        rvOnDelivery.setAdapter(onDeliveryAdapter);
        rvHistory.setAdapter(historyAdapter);

        // Setup tab click listeners
        setupTabListeners();

        // Load orders
        loadOrders();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void setupTabListeners() {
        // On Delivery tab
        View.OnClickListener onDeliveryListener = v -> switchToOnDelivery();
        tabOnDeliveryGradient.setOnClickListener(onDeliveryListener);
        tabOnDeliveryPlain.setOnClickListener(onDeliveryListener);

        // History tab
        View.OnClickListener historyListener = v -> switchToHistory();
        tabHistoryGradient.setOnClickListener(historyListener);
        tabHistoryPlain.setOnClickListener(historyListener);
    }

    private void switchToOnDelivery() {
        rvOnDelivery.setVisibility(View.VISIBLE);
        rvHistory.setVisibility(View.GONE);
        tabOnDeliveryGradient.setVisibility(View.VISIBLE);
        tabOnDeliveryPlain.setVisibility(View.GONE);
        tabHistoryGradient.setVisibility(View.GONE);
        tabHistoryPlain.setVisibility(View.VISIBLE);
    }

    private void switchToHistory() {
        rvOnDelivery.setVisibility(View.GONE);
        rvHistory.setVisibility(View.VISIBLE);
        tabOnDeliveryGradient.setVisibility(View.GONE);
        tabOnDeliveryPlain.setVisibility(View.VISIBLE);
        tabHistoryGradient.setVisibility(View.VISIBLE);
        tabHistoryPlain.setVisibility(View.GONE);
    }

    private void loadOrders() {
        Log.d(TAG, "Loading orders...");
        apiService.getMyOrders().enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful()) {
                    OrderResponse orderResponse = response.body();
                    Log.d(TAG, "Raw response: " + response.raw());
                    Log.d(TAG, "Response body: " + (orderResponse != null ? orderResponse.toString() : "null"));
                    
                    if (orderResponse != null && orderResponse.getOrders() != null) {
                        List<Order> allOrders = orderResponse.getOrders();
                        Log.d(TAG, "Received " + allOrders.size() + " orders");
                        
                        List<Order> pendingOrders = new ArrayList<>();
                        List<Order> historyOrders = new ArrayList<>();

                        for (Order order : allOrders) {
                            Log.d(TAG, "Processing order #" + order.getOrderId() + " with status: " + order.getOrderStatus());
                            if (order.getOrderStatus().equals("Pending")) {
                                pendingOrders.add(order);
                            }
                            historyOrders.add(order);
                        }

                        Log.d(TAG, "Pending orders: " + pendingOrders.size());
                        Log.d(TAG, "History orders: " + historyOrders.size());

                        onDeliveryAdapter.updateOrders(pendingOrders);
                        historyAdapter.updateOrders(historyOrders);
                    } else {
                        Log.e(TAG, "Response body or orders list is null");
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        Toast.makeText(OrderActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                        onDeliveryAdapter.updateOrders(new ArrayList<>());
                        historyAdapter.updateOrders(new ArrayList<>());
                    }
                } else {
                    Log.e(TAG, "Error response: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Toast.makeText(OrderActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                    onDeliveryAdapter.updateOrders(new ArrayList<>());
                    historyAdapter.updateOrders(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e(TAG, "Network error loading orders", t);
                Toast.makeText(OrderActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                onDeliveryAdapter.updateOrders(new ArrayList<>());
                historyAdapter.updateOrders(new ArrayList<>());
            }
        });
    }

    @Override
    public void onExpandClick(Order order, int position) {
        // Handle expand click if needed
    }

    @Override
    public void onCancelClick(Order order) {
        new AlertDialog.Builder(this)
            .setTitle("Cancel Order")
            .setMessage("Are you sure you want to cancel this order?")
            .setPositiveButton("Yes", (dialog, which) -> {
                cancelOrder(order);
            })
            .setNegativeButton("No", null)
            .show();
    }

    private void cancelOrder(Order order) {
        OrderStatusRequest request = new OrderStatusRequest("Cancelled");
        apiService.updateOrderStatus(order.getOrderId(), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadOrders(); // Reload orders after cancellation
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle error
            }
        });
    }

    private void setupBottomNavigation() {
        ImageView ivShop = findViewById(R.id.ivShop);
        ivShop.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, ShopActivity.class));
        });
        ImageView ivBuilder = findViewById(R.id.ivBuilder);
        ivBuilder.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, BuilderActivity.class));
        });
        ImageView ivSetting = findViewById(R.id.ivSetting);
        ivSetting.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, SettingActivity.class));
        });
        ImageView ivCatalog = findViewById(R.id.ivCatalog);
        ivCatalog.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, CatalogActivity.class));
        });
        ImageView ivHome = findViewById(R.id.ivHome);
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, HomeActivity.class));
        });
    }
}
