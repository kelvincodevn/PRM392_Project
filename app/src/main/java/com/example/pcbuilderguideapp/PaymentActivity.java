package com.example.pcbuilderguideapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.models.CartItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import com.example.pcbuilderguideapp.models.CreateOrderDTO;
import com.example.pcbuilderguideapp.models.CreateOrderItemDTO;
import com.example.pcbuilderguideapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.ProgressDialog;
import android.util.Log;
import com.example.pcbuilderguideapp.models.SimpleCartItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView rvPaymentCartItems;
    private TextView tvTotalPaymentPrice;
    private EditText etAddress;
    private Button btnCreateOrder, btnCancelOrder, btnGetLocation;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private List<SimpleCartItem> simpleCartItems = new ArrayList<>();
    private static final String TAG = "PaymentActivity";
    
    // Location-related variables
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        rvPaymentCartItems = findViewById(R.id.rvPaymentCartItems);
        tvTotalPaymentPrice = findViewById(R.id.tvTotalPaymentPrice);
        etAddress = findViewById(R.id.etAddress);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Initialize location services
        initializeLocationServices();

        // Set up location permission launcher
        setupLocationPermissionLauncher();

        // Set up location button click listener
        btnGetLocation.setOnClickListener(v -> getCurrentLocation());

        // Get cart items for display
        Intent intent = getIntent();
        ArrayList<CartItem> displayItems = (ArrayList<CartItem>) intent.getSerializableExtra("cart_items");
        if (displayItems != null) {
            cartItems.addAll(displayItems);
        }
        // Get simple cart items for order creation
        ArrayList<SimpleCartItem> items = (ArrayList<SimpleCartItem>) intent.getSerializableExtra("simple_cart_items");
        if (items != null) {
            simpleCartItems.addAll(items);
            for (SimpleCartItem item : simpleCartItems) {
                android.util.Log.d(TAG, "PaymentActivity SimpleCartItem: productId=" + item.getProductId() + ", quantity=" + item.getQuantity());
            }
        }

        cartAdapter = new CartAdapter(cartItems, true);
        rvPaymentCartItems.setAdapter(cartAdapter);
        rvPaymentCartItems.setLayoutManager(new LinearLayoutManager(this));

        updateTotalPrice();

        btnCreateOrder.setOnClickListener(v -> {
            String address = etAddress.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
                return;
            }
            // Build order items
            java.util.List<CreateOrderItemDTO> orderItems = new java.util.ArrayList<>();
            for (SimpleCartItem item : simpleCartItems) {
                int productId = item.getProductId();
                int quantity = item.getQuantity();
                int thirdPartyId = 1; // As requested
                orderItems.add(new CreateOrderItemDTO(productId, quantity, thirdPartyId));
            }
            // Build order DTO
            String paymentMethod = "Pay when order arrive";
            CreateOrderDTO orderDTO = new CreateOrderDTO(address, paymentMethod, orderItems);

            Log.d(TAG, "Creating order with address: " + address + ", paymentMethod: " + paymentMethod + ", items: " + orderItems.size());
            for (CreateOrderItemDTO item : orderItems) {
                Log.d(TAG, "OrderItem - productId: " + item.getProductId() + ", quantity: " + item.getQuantity() + ", thirdPartyId: " + item.getThirdPartyId());
            }

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Creating order...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            RetrofitClient.getInstance(this)
                .getApiService()
                .createOrder(orderDTO)
                .enqueue(new Callback<com.example.pcbuilderguideapp.models.Order>() {
                    @Override
                    public void onResponse(Call<com.example.pcbuilderguideapp.models.Order> call, Response<com.example.pcbuilderguideapp.models.Order> response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "Order creation response: " + response.code() + ", body: " + response.body());
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(PaymentActivity.this, "Order created!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                errorBody = "Error reading errorBody: " + e.getMessage();
                            }
                            Log.e(TAG, "Order creation failed: " + response.message() + ", errorBody: " + errorBody);
                            Toast.makeText(PaymentActivity.this, "Failed to create order: " + errorBody, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<com.example.pcbuilderguideapp.models.Order> call, Throwable t) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Order creation error: ", t);
                        Toast.makeText(PaymentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });

        btnCancelOrder.setOnClickListener(v -> finish());
    }

    public void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.getProduct() != null) {
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }
        tvTotalPaymentPrice.setText("Total price VND" + String.format("%.2f", total));
    }

    private void initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    private void setupLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                if (result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Show loading state
            btnGetLocation.setEnabled(false);
            btnGetLocation.setText("⏳");
            
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String fullAddress = address.getAddressLine(0);
                                if (fullAddress != null && !fullAddress.isEmpty()) {
                                    etAddress.setText(fullAddress);
                                    Toast.makeText(this, "Location address set successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Could not get address from location", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "No address found for this location", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting address: ", e);
                            Toast.makeText(this, "Error getting address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Location not available. Please enable GPS.", Toast.LENGTH_LONG).show();
                    }
                    // Reset button state
                    btnGetLocation.setEnabled(true);
                    btnGetLocation.setText("📍");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting location: ", e);
                    Toast.makeText(this, "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Reset button state
                    btnGetLocation.setEnabled(true);
                    btnGetLocation.setText("📍");
                });
        } else {
            locationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }
} 