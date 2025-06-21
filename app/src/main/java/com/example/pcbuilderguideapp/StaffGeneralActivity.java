package com.example.pcbuilderguideapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.adapters.OrderAdapter;
import com.example.pcbuilderguideapp.models.Order;
import com.example.pcbuilderguideapp.models.OrderListResponse;
import com.example.pcbuilderguideapp.models.OrderStatusRequest;
import com.example.pcbuilderguideapp.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffGeneralActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {
    private RecyclerView rvOnDelivery, rvHistory;
    private OrderAdapter onDeliveryAdapter, historyAdapter;
    private List<Order> onDeliveryOrders = new ArrayList<>();
    private List<Order> historyOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_general);

        rvOnDelivery = findViewById(R.id.rvOnDelivery);
        rvHistory = findViewById(R.id.rvHistory);

        // TODO: Pass a listener that handles order updates (e.g., confirm, cancel)
        onDeliveryAdapter = new OrderAdapter(onDeliveryOrders, this, true);
        historyAdapter = new OrderAdapter(historyOrders, this, true);

        rvOnDelivery.setLayoutManager(new LinearLayoutManager(this));
        rvOnDelivery.setAdapter(onDeliveryAdapter);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);

        // Tab switching logic
        setupTabClickListeners();

        fetchStaffDeliveries();
    }

    private void setupTabClickListeners() {
        View tabOnDeliveryGradient = findViewById(R.id.tabOnDeliveryGradient);
        View tabOnDeliveryPlain = findViewById(R.id.tabOnDeliveryPlain);
        View tabHistoryGradient = findViewById(R.id.tabHistoryGradient);
        View tabHistoryPlain = findViewById(R.id.tabHistoryPlain);

        View.OnClickListener onDeliveryClick = v -> {
            rvOnDelivery.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
            tabOnDeliveryGradient.setVisibility(View.VISIBLE);
            tabOnDeliveryPlain.setVisibility(View.GONE);
            tabHistoryGradient.setVisibility(View.GONE);
            tabHistoryPlain.setVisibility(View.VISIBLE);
        };
        View.OnClickListener historyClick = v -> {
            rvOnDelivery.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            tabOnDeliveryGradient.setVisibility(View.GONE);
            tabOnDeliveryPlain.setVisibility(View.VISIBLE);
            tabHistoryGradient.setVisibility(View.VISIBLE);
            tabHistoryPlain.setVisibility(View.GONE);
        };
        tabOnDeliveryGradient.setOnClickListener(onDeliveryClick);
        tabOnDeliveryPlain.setOnClickListener(onDeliveryClick);
        tabHistoryGradient.setOnClickListener(historyClick);
        tabHistoryPlain.setOnClickListener(historyClick);
    }

    private void fetchStaffDeliveries() {
        RetrofitClient.getInstance(this)
            .getApiService()
            .getMyDeliveries()
            .enqueue(new Callback<OrderListResponse>() {
                @Override
                public void onResponse(Call<OrderListResponse> call, Response<OrderListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        onDeliveryOrders.clear();
                        historyOrders.clear();
                        for (Order order : response.body().getValues()) {
                            // Filter orders for "On Delivery" and "History"
                            // TODO: Adjust these statuses to match your backend exactly
                            if ("Processing".equalsIgnoreCase(order.getOrderStatus()) || "Pending".equalsIgnoreCase(order.getOrderStatus())) {
                                onDeliveryOrders.add(order);
                            } else {
                                historyOrders.add(order);
                            }
                        }
                        onDeliveryAdapter.notifyDataSetChanged();
                        historyAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(StaffGeneralActivity.this, "Failed to load deliveries", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<OrderListResponse> call, Throwable t) {
                    Toast.makeText(StaffGeneralActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updateOrderStatus(int orderId, String newStatus) {
        OrderStatusRequest request = new OrderStatusRequest(newStatus);
        RetrofitClient.getInstance(this).getApiService().updateOrderStatus(orderId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(StaffGeneralActivity.this, "Order status updated", Toast.LENGTH_SHORT).show();
                    fetchStaffDeliveries(); // Refresh lists
                } else {
                    Toast.makeText(StaffGeneralActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(StaffGeneralActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Confirm", (dialog, which) -> onConfirm.run())
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public void onExpandClick(Order order, int position) {
        // Can be used for extra logic if needed
    }

    @Override
    public void onCancelClick(Order order) {
        showConfirmationDialog("Cancel Order", "Are you sure you want to cancel this order?", () -> {
            updateOrderStatus(order.getOrderId(), "Cancelled");
        });
    }

    @Override
    public void onConfirmClick(Order order) {
        showConfirmationDialog("Confirm Delivery", "Are you sure you want to mark this order as delivered?", () -> {
            updateOrderStatus(order.getOrderId(), "Delivered");
        });
    }
} 