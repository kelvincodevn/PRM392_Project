package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pcbuilderguideapp.utils.TokenManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CreateComponentActivity extends AppCompatActivity {
    private static final String API_URL = "https://pcpb-axhxcdckf8a5a5ed.southeastasia-01.azurewebsites.net/api/Product";
    private static final String CATEGORY_API_URL = "https://pcpb-axhxcdckf8a5a5ed.southeastasia-01.azurewebsites.net/api/Category";
    private EditText etProductName, etDescription, etPrice, etStockQuantity;
    private Spinner spinnerCategory;
    private Button btnConfirm, btnCancel;
    private List<Category> categories = new ArrayList<>();
    
    // Image upload variables
    private ImageView ivProductImage;
    private LinearLayout llUploadOverlay;
    private Button btnUploadImage;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_component);

        try {
            // Trust all certificates for development
            trustAllCertificates();

            // Initialize views
            initializeViews();

            // Initialize image picker launcher
            initializeImagePicker();

            // Set up back button
            ImageView backButton = findViewById(R.id.ivBack);
            if (backButton != null) {
                backButton.setOnClickListener(v -> finish());
            }

            // Set up confirm button
            if (btnConfirm != null) {
                btnConfirm.setOnClickListener(v -> createProduct());
            }

            // Set up cancel button
            if (btnCancel != null) {
                btnCancel.setOnClickListener(v -> finish());
            }

            // Set up image upload functionality
            setupImageUpload();

            // Load categories
            loadCategories();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing create component: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            etProductName = findViewById(R.id.etProductName);
            etDescription = findViewById(R.id.etDescription);
            etPrice = findViewById(R.id.etPrice);
            etStockQuantity = findViewById(R.id.etStockQuantity);
            spinnerCategory = findViewById(R.id.spinnerCategory);
            btnConfirm = findViewById(R.id.btnConfirm);
            btnCancel = findViewById(R.id.btnCancel);
            ivProductImage = findViewById(R.id.ivProductImage);
            llUploadOverlay = findViewById(R.id.llUploadOverlay);
            btnUploadImage = findViewById(R.id.btnUploadImage);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        loadImageFromUri(selectedImageUri);
                        llUploadOverlay.setVisibility(View.GONE);
                        Toast.makeText(this, "Image selected successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }

    private void setupImageUpload() {
        // Set up upload button click listener
        btnUploadImage.setOnClickListener(v -> openImagePicker());
        
        // Set up image preview area click listener
        ivProductImage.setOnClickListener(v -> openImagePicker());
        llUploadOverlay.setOnClickListener(v -> openImagePicker());
    }
    
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadImageFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                ivProductImage.setImageBitmap(bitmap);
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            Log.e("CreateComponentActivity", "Error loading image: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCategories() {
        Log.d("CreateComponentActivity", "Loading categories from: " + CATEGORY_API_URL);
        
        // Get authentication token
        String token = TokenManager.getInstance(this).getToken();
        if (token == null) {
            Log.e("CreateComponentActivity", "No authentication token available");
            Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show();
            addDefaultCategories();
            return;
        }
        
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET,
            CATEGORY_API_URL,
            null,
            response -> {
                try {
                    Log.d("CreateComponentActivity", "Categories response: " + response.toString());
                    categories.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject categoryJson = response.getJSONObject(i);
                        Category category = new Category(
                            categoryJson.getInt("categoryId"),
                            categoryJson.getString("categoryName")
                        );
                        categories.add(category);
                        Log.d("CreateComponentActivity", "Added category: " + category.getName());
                    }
                    updateCategorySpinner();
                } catch (JSONException e) {
                    Log.e("CreateComponentActivity", "Error parsing categories: " + e.getMessage(), e);
                    Toast.makeText(CreateComponentActivity.this, "Error parsing categories: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Add some default categories as fallback
                    addDefaultCategories();
                }
            },
            error -> {
                Log.e("CreateComponentActivity", "Error loading categories: " + error.getMessage(), error);
                Toast.makeText(CreateComponentActivity.this, "Error loading categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // Add some default categories as fallback
                addDefaultCategories();
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(request);
    }

    private void addDefaultCategories() {
        Log.d("CreateComponentActivity", "Adding default categories");
        categories.clear();
        categories.add(new Category(1, "CPU"));
        categories.add(new Category(2, "GPU"));
        categories.add(new Category(3, "Memory (RAM)"));
        categories.add(new Category(4, "Storage (SSD)"));
        categories.add(new Category(5, "Motherboard"));
        categories.add(new Category(6, "PSU"));
        categories.add(new Category(7, "Case"));
        categories.add(new Category(8, "Cooler"));
        categories.add(new Category(9, "Monitor"));
        categories.add(new Category(10, "Keyboard"));
        categories.add(new Category(11, "Mouse"));
        categories.add(new Category(12, "Headset"));
        updateCategorySpinner();
    }

    private void updateCategorySpinner() {
        try {
            List<String> categoryNames = new ArrayList<>();
            // Add a default "Select Category" option
            categoryNames.add("Select Category");
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }

            Log.d("CreateComponentActivity", "Updating spinner with " + categoryNames.size() + " categories");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            
            if (spinnerCategory != null) {
                spinnerCategory.setAdapter(adapter);
                Log.d("CreateComponentActivity", "Spinner adapter set successfully");
            } else {
                Log.e("CreateComponentActivity", "Spinner is null!");
            }
        } catch (Exception e) {
            Log.e("CreateComponentActivity", "Error updating spinner: " + e.getMessage(), e);
            Toast.makeText(this, "Error updating category spinner: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createProduct() {
        try {
            // Get authentication token
            String token = TokenManager.getInstance(this).getToken();
            if (token == null) {
                Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Get selected category (subtract 1 because we added "Select Category" as first item)
            int selectedPosition = spinnerCategory.getSelectedItemPosition();
            if (selectedPosition <= 0 || selectedPosition > categories.size()) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }
            Category selectedCategory = categories.get(selectedPosition - 1); // Subtract 1 for the "Select Category" option

            JSONObject productJson = new JSONObject();
            productJson.put("productName", etProductName.getText().toString());
            productJson.put("description", etDescription.getText().toString());
            productJson.put("price", Double.parseDouble(etPrice.getText().toString()));
            productJson.put("stockQuantity", Integer.parseInt(etStockQuantity.getText().toString()));
            productJson.put("categoryId", selectedCategory.getId());
            productJson.put("imageUrl", ""); // TODO: Implement image upload

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_URL,
                productJson,
                response -> {
                    Toast.makeText(CreateComponentActivity.this, "Product created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    Toast.makeText(CreateComponentActivity.this, "Error creating product: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);
        } catch (JSONException e) {
            Toast.makeText(this, "Error preparing product data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper class to store category data
    private static class Category {
        private final int id;
        private final String name;

        public Category(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}