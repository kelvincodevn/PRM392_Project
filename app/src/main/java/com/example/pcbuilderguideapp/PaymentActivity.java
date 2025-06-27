package com.example.pcbuilderguideapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.models.CartItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.pcbuilderguideapp.models.CreateOrderDTO;
import com.example.pcbuilderguideapp.models.CreateOrderItemDTO;
import com.example.pcbuilderguideapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.momo.momo_partner.AppMoMoLib;

import android.app.ProgressDialog;
import android.util.Log;
import com.example.pcbuilderguideapp.models.SimpleCartItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView rvPaymentCartItems;
    private TextView tvTotalPaymentPrice;
    private EditText etAddress;
    private Button btnCreateOrder, btnCancelOrder, btnGetLocation;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private List<SimpleCartItem> simpleCartItems = new ArrayList<>();
    private static final String TAG = "PaymentActivity";
    private RadioButton rbMomo;


    //layout confirm order activity
    private String amount = "10000";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "PCPB";
    private String merchantCode = "MOMO"; // Updated to use MOMO test code
    private String merchantNameLabel = "Nhà cung cấp";
    private String description = "Mua hàng qua momo";
    
    // Test mode flag - set to true to bypass MoMo payment for testing
    private boolean testMode = true;
    
    // Location-related variables
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private LocationManager locationManager;

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
        rbMomo = findViewById(R.id.rbMomo);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Initialize location services
        initializeLocationServices();

        // Set up location permission launcher
        setupLocationPermissionLauncher();

        // Set up location button click listener
        btnGetLocation.setOnClickListener(v -> getCurrentLocation());
        //Set enviroment for momo
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

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

            // Check if MoMo payment is selected
            if (rbMomo.isChecked()) {
                if (testMode) {
                    // Test mode: Skip MoMo payment and create order directly
                    Toast.makeText(this, "Test mode: Creating order with MoMo payment (bypassed)", Toast.LENGTH_SHORT).show();
                    createOrderWithPaymentMethod("MoMo Payment (Test Mode)");
                } else {
                    // Calculate total amount for MoMo payment
                    double totalAmount = 0;
                    for (CartItem item : cartItems) {
                        if (item.getProduct() != null) {
                            totalAmount += item.getProduct().getPrice() * item.getQuantity();
                        }
                    }
                    
                    // Update amount for MoMo payment (convert to integer as required by MoMo)
                    amount = String.valueOf((int) totalAmount);
                    
                    // Generate unique order ID for MoMo
                    String orderId = "PCPB_" + System.currentTimeMillis();
                    
                    // Request MoMo payment
                    requestPayment(orderId);
                }
            } else {
                // Regular order creation without MoMo payment
                createOrderWithPaymentMethod("Pay when order arrive");
            }
        });

        btnCancelOrder.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update button state based on current GPS status
        updateLocationButtonState();
        
        // Check if GPS was enabled while user was in settings
        if (isGPSEnabled() && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // If GPS is now enabled and we have permission, try to get location automatically
            if (etAddress.getText().toString().trim().isEmpty()) {
                // Only auto-get location if address field is empty
                getCurrentLocation();
            }
        }
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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        updateLocationButtonState();
    }

    private void updateLocationButtonState() {
        if (btnGetLocation != null) {
            if (isGPSEnabled()) {
                btnGetLocation.setText("📍");
                btnGetLocation.setEnabled(true);
                btnGetLocation.setAlpha(1.0f);
            } else {
                btnGetLocation.setText("📍");
                btnGetLocation.setEnabled(true);
                btnGetLocation.setAlpha(0.6f);
            }
        }
    }

    private void setupLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                if (result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                    getCurrentLocation();
                } else {
                    showLocationPermissionDialog();
                }
            }
        );
    }

    private void showLocationPermissionDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Location Permission Required")
            .setMessage("This app needs location permission to automatically fill your delivery address. Without this permission, you'll need to enter your address manually.\n\nWould you like to grant location permission?")
            .setPositiveButton("Grant Permission", (dialog, which) -> {
                locationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            })
            .setNegativeButton("Enter Manually", (dialog, which) -> {
                dialog.dismiss();
                Toast.makeText(this, "Please enter your address manually", Toast.LENGTH_SHORT).show();
            })
            .setCancelable(false)
            .show();
    }

    private boolean isGPSEnabled() {
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showGPSEnableDialog() {
        new AlertDialog.Builder(this)
            .setTitle("GPS Not Available")
            .setMessage("GPS is currently disabled on your device. To automatically fill your delivery address with your current location, please enable GPS in your device settings.\n\nWould you like to open location settings now?")
            .setPositiveButton("Open Settings", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            })
            .setNegativeButton("Enter Manually", (dialog, which) -> {
                dialog.dismiss();
                Toast.makeText(this, "Please enter your address manually", Toast.LENGTH_SHORT).show();
            })
            .setNeutralButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }

    private void getCurrentLocation() {
        // First check if GPS is enabled
        if (!isGPSEnabled()) {
            showGPSEnableDialog();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Show loading state
            btnGetLocation.setEnabled(false);
            btnGetLocation.setText("⏳");
            
            // Create location request with high priority
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .build();
            
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        processLocation(location);
                    } else {
                        // Try to get last known location as fallback
                        fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, lastLocation -> {
                                if (lastLocation != null) {
                                    // Process last known location
                                    processLocation(lastLocation);
                                } else {
                                    Toast.makeText(this, "Location not available. Please check GPS settings and try again.", Toast.LENGTH_LONG).show();
                                }
                                // Reset button state
                                btnGetLocation.setEnabled(true);
                                btnGetLocation.setText("📍");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error getting last location: ", e);
                                Toast.makeText(this, "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                // Reset button state
                                btnGetLocation.setEnabled(true);
                                btnGetLocation.setText("📍");
                            });
                        return;
                    }
                    // Reset button state
                    btnGetLocation.setEnabled(true);
                    btnGetLocation.setText("📍");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting current location: ", e);
                    Toast.makeText(this, "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Reset button state
                    btnGetLocation.setEnabled(true);
                    btnGetLocation.setText("📍");
                });
        } else {
            locationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }
    //payment token momo
    //Get token through MoMo app
    private void requestPayment(String orderId) {
        try {
            AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
            AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);

            Map<String, Object> eventValue = new HashMap<>();
            //client Required
            eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
            eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
            eventValue.put("amount", amount); //Kiểu integer
            eventValue.put("orderId", orderId); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
            eventValue.put("orderLabel", orderId); //gán nhãn

            //client Optional - bill info
            eventValue.put("merchantnamelabel", merchantNameLabel);//gán nhãn
            eventValue.put("fee", fee); //Kiểu integer
            eventValue.put("description", description); //mô tả đơn hàng - short description

            //client extra data
            eventValue.put("requestId", merchantCode + "merchant_billId_" + System.currentTimeMillis());
            eventValue.put("partnerCode", merchantCode);
            
            //Example extra data
            JSONObject objExtraData = new JSONObject();
            try {
                objExtraData.put("site_code", "PCPB");
                objExtraData.put("site_name", "PC Builder App");
                objExtraData.put("order_type", "PC Components");
                objExtraData.put("items_count", simpleCartItems.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            eventValue.put("extraData", objExtraData.toString());

            eventValue.put("extra", "");
            
            // Log payment request details for debugging
            Log.d("MoMoPayment", "Requesting payment with:");
            Log.d("MoMoPayment", "Merchant Code: " + merchantCode);
            Log.d("MoMoPayment", "Amount: " + amount);
            Log.d("MoMoPayment", "Order ID: " + orderId);
            Log.d("MoMoPayment", "Environment: DEVELOPMENT");
            
            AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);
        } catch (Exception e) {
            Log.e("MoMoPayment", "Error requesting MoMo payment: ", e);
            Toast.makeText(this, "Error initiating MoMo payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            // Fallback: Create order without MoMo payment
            new AlertDialog.Builder(this)
                .setTitle("MoMo Payment Error")
                .setMessage("MoMo payment failed to initialize. Would you like to create the order with cash on delivery instead?")
                .setPositiveButton("Yes, Use Cash on Delivery", (dialog, which) -> {
                    createOrderWithPaymentMethod("Pay when order arrive");
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        }
    }

    //Get token callback from MoMo app an submit to server side
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Log.d("MoMoPayment", "Payment successful: " + data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // MoMo payment successful, create order with MoMo payment method
                        Log.d("MoMoPayment", "Token received: " + token);
                        Log.d("MoMoPayment", "Phone number: " + phoneNumber);
                        Log.d("MoMoPayment", "Environment: " + env);
                        
                        // Show success message with transaction details
                        String successMessage = "MoMo payment successful!\n" +
                            "Phone: " + phoneNumber + "\n" +
                            "Amount: " + amount + " VND\n" +
                            "Token: " + token.substring(0, Math.min(10, token.length())) + "...";
                        
                        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();
                        
                        // Show instructions for viewing test transactions
                        showTestTransactionInstructions();
                        
                        createOrderWithPaymentMethod("MoMo Payment");
                    } else {
                        Log.d("MoMoPayment", "Payment failed: No token received");
                        Toast.makeText(this, "MoMo payment failed: No token received", Toast.LENGTH_SHORT).show();
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null ? data.getStringExtra("message") : "Payment failed";
                    Log.d("MoMoPayment", "Payment failed: " + message);
                    Toast.makeText(this, "MoMo payment failed: " + message, Toast.LENGTH_SHORT).show();
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Log.d("MoMoPayment", "Payment cancelled by user");
                    Toast.makeText(this, "MoMo payment cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    //TOKEN FAIL
                    Log.d("MoMoPayment", "Payment failed with unknown status");
                    Toast.makeText(this, "MoMo payment failed with unknown error", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("MoMoPayment", "Payment failed: No data received");
                Toast.makeText(this, "MoMo payment failed: No data received", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("MoMoPayment", "Payment failed: Invalid request code or result code");
            Toast.makeText(this, "MoMo payment failed: Invalid response", Toast.LENGTH_SHORT).show();
        }
    }

    private void createOrderWithPaymentMethod(String paymentMethod) {
        // Build order items
        java.util.List<CreateOrderItemDTO> orderItems = new java.util.ArrayList<>();
        for (SimpleCartItem item : simpleCartItems) {
            int productId = item.getProductId();
            int quantity = item.getQuantity();
            int thirdPartyId = 1; // As requested
            orderItems.add(new CreateOrderItemDTO(productId, quantity, thirdPartyId));
        }
        
        // Build order DTO
        CreateOrderDTO orderDTO = new CreateOrderDTO(etAddress.getText().toString().trim(), paymentMethod, orderItems);

        Log.d(TAG, "Creating order with address: " + etAddress.getText().toString().trim() + ", paymentMethod: " + paymentMethod + ", items: " + orderItems.size());
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
                        Toast.makeText(PaymentActivity.this, "Order created successfully!", Toast.LENGTH_SHORT).show();
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
    }

    private void processLocation(Location location) {
        try {
            // Try with default locale first
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
            if (addresses != null && !addresses.isEmpty()) {
                // Try to find the best address
                String bestAddress = getBestAddress(addresses);
                if (bestAddress != null && !bestAddress.isEmpty() && !isGeohashCode(bestAddress)) {
                    etAddress.setText(bestAddress);
                    Toast.makeText(this, "Location address set successfully!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            
            // If first attempt failed, try with Vietnamese locale
            try {
                Geocoder vietnameseGeocoder = new Geocoder(this, new Locale("vi", "VN"));
                List<Address> vietnameseAddresses = vietnameseGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
                if (vietnameseAddresses != null && !vietnameseAddresses.isEmpty()) {
                    String bestAddress = getBestAddress(vietnameseAddresses);
                    if (bestAddress != null && !bestAddress.isEmpty() && !isGeohashCode(bestAddress)) {
                        etAddress.setText(bestAddress);
                        Toast.makeText(this, "Location address set successfully!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "Vietnamese geocoding failed: " + e.getMessage());
            }
            
            // If still no good address, try with English locale
            try {
                Geocoder englishGeocoder = new Geocoder(this, Locale.ENGLISH);
                List<Address> englishAddresses = englishGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
                if (englishAddresses != null && !englishAddresses.isEmpty()) {
                    String bestAddress = getBestAddress(englishAddresses);
                    if (bestAddress != null && !bestAddress.isEmpty() && !isGeohashCode(bestAddress)) {
                        etAddress.setText(bestAddress);
                        Toast.makeText(this, "Location address set successfully!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "English geocoding failed: " + e.getMessage());
            }
            
            // Try with a slightly offset location to get different results
            try {
                // Try with a small offset (about 50 meters)
                double offsetLat = location.getLatitude() + 0.0005;
                double offsetLng = location.getLongitude() + 0.0005;
                
                List<Address> offsetAddresses = geocoder.getFromLocation(offsetLat, offsetLng, 3);
                if (offsetAddresses != null && !offsetAddresses.isEmpty()) {
                    String bestAddress = getBestAddress(offsetAddresses);
                    if (bestAddress != null && !bestAddress.isEmpty() && !isGeohashCode(bestAddress)) {
                        etAddress.setText(bestAddress);
                        Toast.makeText(this, "Location address set successfully!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "Offset geocoding failed: " + e.getMessage());
            }
            
            // If all attempts failed, show coordinates as fallback
            String coordinateAddress = String.format(Locale.getDefault(), 
                "Lat: %.6f, Lng: %.6f (Please enter your address manually)", 
                location.getLatitude(), location.getLongitude());
            etAddress.setText(coordinateAddress);
            Toast.makeText(this, "Could not get exact address. Please edit the coordinates.", Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting address: ", e);
            // Show coordinates as fallback
            String coordinateAddress = String.format(Locale.getDefault(), 
                "Lat: %.6f, Lng: %.6f (Please enter your address manually)", 
                location.getLatitude(), location.getLongitude());
            etAddress.setText(coordinateAddress);
            Toast.makeText(this, "Error getting address. Please enter your address manually.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getBestAddress(List<Address> addresses) {
        for (Address address : addresses) {
            // Log all address components for debugging
            logAddressComponents(address);
            
            String fullAddress = buildFullAddress(address);
            if (fullAddress != null && !fullAddress.isEmpty()) {
                // Check if the address contains geohash codes
                if (containsGeohashCode(fullAddress)) {
                    Log.d(TAG, "Address contains geohash, rebuilding from components");
                    String rebuiltAddress = rebuildAddressFromComponents(address);
                    if (rebuiltAddress != null && !rebuiltAddress.isEmpty()) {
                        return rebuiltAddress;
                    }
                }
                
                // Clean the address to remove geohash codes
                String cleanedAddress = cleanAddress(fullAddress);
                if (cleanedAddress != null && !cleanedAddress.isEmpty() && !isGeohashCode(cleanedAddress)) {
                    return cleanedAddress;
                }
            }
        }
        return null;
    }

    private void logAddressComponents(Address address) {
        Log.d(TAG, "=== Address Components ===");
        Log.d(TAG, "AddressLine(0): " + address.getAddressLine(0));
        Log.d(TAG, "FeatureName: " + address.getFeatureName());
        Log.d(TAG, "Premises: " + address.getPremises());
        Log.d(TAG, "SubThoroughfare: " + address.getSubThoroughfare());
        Log.d(TAG, "Thoroughfare: " + address.getThoroughfare());
        Log.d(TAG, "SubLocality: " + address.getSubLocality());
        Log.d(TAG, "Locality: " + address.getLocality());
        Log.d(TAG, "AdminArea: " + address.getAdminArea());
        Log.d(TAG, "CountryName: " + address.getCountryName());
        Log.d(TAG, "PostalCode: " + address.getPostalCode());
        Log.d(TAG, "Phone: " + address.getPhone());
        Log.d(TAG, "URL: " + address.getUrl());
        Log.d(TAG, "========================");
    }

    private boolean containsGeohashCode(String address) {
        return address != null && (
            address.contains("RRRP+9VV") ||
            address.matches(".*[23456789CFGHJMPQRVWX]{4,12}\\+[23456789CFGHJMPQRVWX]{2,4}.*")
        );
    }

    private String rebuildAddressFromComponents(Address address) {
        StringBuilder rebuiltAddress = new StringBuilder();
        boolean hasComponent = false;
        
        // Try to get feature name or premises first
        if (address.getFeatureName() != null && !address.getFeatureName().isEmpty()) {
            rebuiltAddress.append(address.getFeatureName());
            hasComponent = true;
        } else if (address.getPremises() != null && !address.getPremises().isEmpty()) {
            rebuiltAddress.append(address.getPremises());
            hasComponent = true;
        }
        
        // Add street number
        if (address.getSubThoroughfare() != null && !address.getSubThoroughfare().isEmpty()) {
            if (hasComponent) rebuiltAddress.append(", ");
            rebuiltAddress.append(address.getSubThoroughfare());
            hasComponent = true;
        }
        
        // Add street name
        if (address.getThoroughfare() != null && !address.getThoroughfare().isEmpty()) {
            if (hasComponent) rebuiltAddress.append(" ");
            rebuiltAddress.append(address.getThoroughfare());
            hasComponent = true;
        }
        
        // Add sub-locality (district, ward)
        if (address.getSubLocality() != null && !address.getSubLocality().isEmpty()) {
            if (hasComponent) rebuiltAddress.append(", ");
            rebuiltAddress.append(address.getSubLocality());
            hasComponent = true;
        }
        
        // Add locality (city)
        if (address.getLocality() != null && !address.getLocality().isEmpty()) {
            if (hasComponent) rebuiltAddress.append(", ");
            rebuiltAddress.append(address.getLocality());
            hasComponent = true;
        }
        
        // Add administrative area (province/state)
        if (address.getAdminArea() != null && !address.getAdminArea().isEmpty()) {
            if (hasComponent) rebuiltAddress.append(", ");
            rebuiltAddress.append(address.getAdminArea());
            hasComponent = true;
        }
        
        // Add country
        if (address.getCountryName() != null && !address.getCountryName().isEmpty()) {
            if (hasComponent) rebuiltAddress.append(", ");
            rebuiltAddress.append(address.getCountryName());
            hasComponent = true;
        }
        
        String result = hasComponent ? rebuiltAddress.toString() : null;
        Log.d(TAG, "Rebuilt address from components: " + result);
        return result;
    }

    private String cleanAddress(String address) {
        if (address == null || address.isEmpty()) {
            return null;
        }
        
        Log.d(TAG, "Original address: " + address);
        
        String cleaned = address;
        
        // Remove specific geohash patterns
        // Remove RRRP+9VV specifically
        cleaned = cleaned.replaceAll("RRRP\\+9VV,?\\s*", "");
        Log.d(TAG, "After RRRP+9VV removal: " + cleaned);
        
        // Remove other Plus Code patterns
        cleaned = cleaned.replaceAll("[23456789CFGHJMPQRVWX]{4}\\+[23456789CFGHJMPQRVWX]{3},?\\s*", "");
        Log.d(TAG, "After Plus Code removal: " + cleaned);
        
        // Remove any remaining geohash patterns
        cleaned = cleaned.replaceAll("[23456789CFGHJMPQRVWX]{8}\\+[23456789CFGHJMPQRVWX]{2,3},?\\s*", "");
        cleaned = cleaned.replaceAll("[23456789CFGHJMPQRVWX]{6}\\+[23456789CFGHJMPQRVWX]{3},?\\s*", "");
        cleaned = cleaned.replaceAll("[23456789CFGHJMPQRVWX]{4}\\+[23456789CFGHJMPQRVWX]{4},?\\s*", "");
        
        // Remove any standalone geohash codes
        cleaned = cleaned.replaceAll("^[23456789CFGHJMPQRVWX]{4,12}\\+[23456789CFGHJMPQRVWX]{2,4}\\s*", "");
        cleaned = cleaned.replaceAll("\\s+[23456789CFGHJMPQRVWX]{4,12}\\+[23456789CFGHJMPQRVWX]{2,4}\\s*", " ");
        
        // Clean up extra commas and spaces
        cleaned = cleaned.replaceAll("^\\s*,\\s*", ""); // Remove leading comma
        cleaned = cleaned.replaceAll("\\s*,\\s*$", ""); // Remove trailing comma
        cleaned = cleaned.replaceAll("\\s+", " "); // Replace multiple spaces with single space
        cleaned = cleaned.trim();
        
        Log.d(TAG, "Final cleaned address: " + cleaned);
        
        return cleaned.isEmpty() ? null : cleaned;
    }

    private boolean isGeohashCode(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        
        // Check if the address is a Plus Code (geohash) format
        // Plus codes typically contain 8-10 characters with + symbol
        // Also check for various geohash patterns
        return address.matches("^[23456789CFGHJMPQRVWX]{8}\\+[23456789CFGHJMPQRVWX]{2,3}$") ||
               address.matches("^[23456789CFGHJMPQRVWX]{6}\\+[23456789CFGHJMPQRVWX]{3}$") ||
               address.matches("^[23456789CFGHJMPQRVWX]{4}\\+[23456789CFGHJMPQRVWX]{4}$") ||
               address.matches("^[23456789CFGHJMPQRVWX]{10,12}$") ||
               address.matches("^[A-Z0-9]{8,12}$") ||
               address.contains("+") && address.length() <= 15;
    }

    private String buildFullAddress(Address address) {
        StringBuilder fullAddress = new StringBuilder();
        
        // Try to get the formatted address line first
        if (address.getAddressLine(0) != null && !address.getAddressLine(0).isEmpty()) {
            String addressLine = address.getAddressLine(0);
            // Check if it's not a geohash code
            if (!isGeohashCode(addressLine)) {
                return addressLine;
            }
        }
        
        // Build address from components if address line is not available or is geohash
        boolean hasComponent = false;
        
        // Add street number and name
        if (address.getSubThoroughfare() != null && !address.getSubThoroughfare().isEmpty()) {
            fullAddress.append(address.getSubThoroughfare());
            hasComponent = true;
        }
        
        if (address.getThoroughfare() != null && !address.getThoroughfare().isEmpty()) {
            if (hasComponent) fullAddress.append(" ");
            fullAddress.append(address.getThoroughfare());
            hasComponent = true;
        }
        
        // Add sub-locality (district, ward)
        if (address.getSubLocality() != null && !address.getSubLocality().isEmpty()) {
            if (hasComponent) fullAddress.append(", ");
            fullAddress.append(address.getSubLocality());
            hasComponent = true;
        }
        
        // Add locality (city)
        if (address.getLocality() != null && !address.getLocality().isEmpty()) {
            if (hasComponent) fullAddress.append(", ");
            fullAddress.append(address.getLocality());
            hasComponent = true;
        }
        
        // Add administrative area (province/state)
        if (address.getAdminArea() != null && !address.getAdminArea().isEmpty()) {
            if (hasComponent) fullAddress.append(", ");
            fullAddress.append(address.getAdminArea());
            hasComponent = true;
        }
        
        // Add country
        if (address.getCountryName() != null && !address.getCountryName().isEmpty()) {
            if (hasComponent) fullAddress.append(", ");
            fullAddress.append(address.getCountryName());
            hasComponent = true;
        }
        
        String result = hasComponent ? fullAddress.toString() : null;
        
        // Try to detect and format specific locations
        if (result != null) {
            String formattedResult = formatSpecificLocation(result, address);
            if (formattedResult != null) {
                return formattedResult;
            }
        }
        
        return result;
    }

    private String formatSpecificLocation(String address, Address addressObj) {
        // Check if this is in the Vihomes Grandpark area
        if (address.contains("Long Thạnh Mỹ") || address.contains("Thủ Đức") || address.contains("Nguyễn Xiển")) {
            // This appears to be in the Vihomes Grandpark area
            // Try to extract or suggest a proper address format
            StringBuilder formattedAddress = new StringBuilder();
            
            // Check if we have any feature name or premise
            if (addressObj.getFeatureName() != null && !addressObj.getFeatureName().isEmpty()) {
                formattedAddress.append(addressObj.getFeatureName());
            } else if (addressObj.getPremises() != null && !addressObj.getPremises().isEmpty()) {
                formattedAddress.append(addressObj.getPremises());
            } else {
                // Default to a generic format for the area
                formattedAddress.append("Vihomes Grandpark");
            }
            
            // Add street information if available
            if (addressObj.getThoroughfare() != null && !addressObj.getThoroughfare().isEmpty()) {
                formattedAddress.append(", ").append(addressObj.getThoroughfare());
            }
            
            // Add sub-locality
            if (addressObj.getSubLocality() != null && !addressObj.getSubLocality().isEmpty()) {
                formattedAddress.append(", ").append(addressObj.getSubLocality());
            }
            
            // Add locality
            if (addressObj.getLocality() != null && !addressObj.getLocality().isEmpty()) {
                formattedAddress.append(", ").append(addressObj.getLocality());
            }
            
            // Add admin area
            if (addressObj.getAdminArea() != null && !addressObj.getAdminArea().isEmpty()) {
                formattedAddress.append(", ").append(addressObj.getAdminArea());
            }
            
            return formattedAddress.toString();
        }
        
        return null;
    }

    private void showTestTransactionInstructions() {
        new AlertDialog.Builder(this)
            .setTitle("Test Transaction Completed")
            .setMessage("Your MoMo test payment was successful!\n\n" +
                "To view test transactions:\n" +
                "1. Open MoMo UAT app\n" +
                "2. Go to Transaction History\n" +
                "3. Look for transactions with amount: " + amount + " VND\n\n" +
                "Note: This is a test transaction and won't appear in the real MoMo app.")
            .setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
            })
            .setCancelable(false)
            .show();
    }
} 