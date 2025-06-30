package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
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
import java.io.File;
import java.io.FileOutputStream;
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
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;
import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.Callback;

public class UpdateComponentActivity extends AppCompatActivity {
    private static final String API_URL = "https://pcpb-axhxcdckf8a5a5ed.southeastasia-01.azurewebsites.net/api/Product";
    private static final String CATEGORY_API_URL = "https://pcpb-axhxcdckf8a5a5ed.southeastasia-01.azurewebsites.net/api/Category";
    
    private EditText etProductName, etDescription, etPrice, etStockQuantity;
    private Spinner spinnerCategory;
    private Button btnUpdate, btnCancel;
    private List<Category> categories = new ArrayList<>();
    private int productId;
    private Product currentProduct;
    private Uri selectedImageUri;
    private ImageView ivProductImage;
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_component);

        try {
            // Get product ID from intent
            productId = getIntent().getIntExtra("product_id", -1);
            if (productId == -1) {
                Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Trust all certificates for development
            trustAllCertificates();

            // Initialize views
            initializeViews();

            // Set up back button
            ImageView backButton = findViewById(R.id.ivBack);
            if (backButton != null) {
                backButton.setOnClickListener(v -> finish());
            }

            // Set up update button
            if (btnUpdate != null) {
                btnUpdate.setOnClickListener(v -> updateProduct());
            }

            // Set up cancel button
            if (btnCancel != null) {
                btnCancel.setOnClickListener(v -> finish());
            }

            // Load categories and product data
            loadCategories();
            loadProductData();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing update component: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            btnUpdate = findViewById(R.id.btnUpdate);
            btnCancel = findViewById(R.id.btnCancel);
            ivProductImage = findViewById(R.id.ivProductImage);
            if (ivProductImage != null) {
                ivProductImage.setOnClickListener(v -> openImagePicker());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadProductData() {
        String token = TokenManager.getInstance(this).getToken();
        if (token == null) {
            Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            API_URL + "/" + productId,
            null,
            response -> {
                try {
                    Log.d("UpdateComponentActivity", "Product data: " + response.toString());
                    currentProduct = new Product(
                        response.getInt("productId"),
                        response.getString("productName"),
                        response.optString("description", ""),
                        response.getDouble("price"),
                        response.getInt("stockQuantity"),
                        response.optString("imageUrl", ""),
                        response.optString("companyName", "")
                    );
                    populateFields();
                } catch (JSONException e) {
                    Log.e("UpdateComponentActivity", "Error parsing product data: " + e.getMessage(), e);
                    Toast.makeText(this, "Error loading product data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e("UpdateComponentActivity", "Error loading product data: " + error.getMessage(), error);
                Toast.makeText(this, "Error loading product data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void populateFields() {
        if (currentProduct != null) {
            etProductName.setText(currentProduct.getName());
            etDescription.setText(currentProduct.getDescription());
            etPrice.setText(String.valueOf(currentProduct.getPrice()));
            etStockQuantity.setText(String.valueOf(currentProduct.getStockQuantity()));
        }
    }

    private void loadCategories() {
        Log.d("UpdateComponentActivity", "Loading categories from: " + CATEGORY_API_URL);
        
        String token = TokenManager.getInstance(this).getToken();
        if (token == null) {
            Log.e("UpdateComponentActivity", "No authentication token available");
            Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show();
            addDefaultCategories();
            return;
        }
        
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            CATEGORY_API_URL,
            null,
            response -> {
                try {
                    Log.d("UpdateComponentActivity", "Categories response: " + response.toString());
                    JSONArray valuesArray = response.getJSONArray("$values");
                    categories.clear();
                    for (int i = 0; i < valuesArray.length(); i++) {
                        JSONObject categoryJson = valuesArray.getJSONObject(i);
                        if (categoryJson.has("categoryId")) {
                            Category category = new Category(
                                categoryJson.getInt("categoryId"),
                                categoryJson.getString("categoryName")
                            );
                            categories.add(category);
                            Log.d("UpdateComponentActivity", "Added category: " + category.getName());
                        } else {
                            Log.d("UpdateComponentActivity", "Skipping non-category object: " + categoryJson.toString());
                        }
                    }
                    updateCategorySpinner();
                } catch (JSONException e) {
                    Log.e("UpdateComponentActivity", "Error parsing categories: " + e.getMessage(), e);
                    Toast.makeText(UpdateComponentActivity.this, "Error parsing categories: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    addDefaultCategories();
                }
            },
            error -> {
                Log.e("UpdateComponentActivity", "Error loading categories: " + error.getMessage(), error);
                Toast.makeText(UpdateComponentActivity.this, "Error loading categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        Log.d("UpdateComponentActivity", "Adding default categories");
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
            categoryNames.add("Select Category");
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }

            Log.d("UpdateComponentActivity", "Updating spinner with " + categoryNames.size() + " categories");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            
            if (spinnerCategory != null) {
                spinnerCategory.setAdapter(adapter);
                Log.d("UpdateComponentActivity", "Spinner adapter set successfully");
            } else {
                Log.e("UpdateComponentActivity", "Spinner is null!");
            }
        } catch (Exception e) {
            Log.e("UpdateComponentActivity", "Error updating spinner: " + e.getMessage(), e);
            Toast.makeText(this, "Error updating category spinner: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivProductImage.setImageURI(selectedImageUri);
        }
    }

    private void updateProduct() {
        try {
            String token = TokenManager.getInstance(this).getToken();
            if (token == null) {
                Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show();
                return;
            }
            int selectedPosition = spinnerCategory.getSelectedItemPosition();
            if (selectedPosition <= 0 || selectedPosition > categories.size()) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }
            Category selectedCategory = categories.get(selectedPosition - 1);

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            builder.addFormDataPart("productId", String.valueOf(productId));
            builder.addFormDataPart("thirdPartyId", "1");
            builder.addFormDataPart("productName", etProductName.getText().toString());
            builder.addFormDataPart("description", etDescription.getText().toString());
            builder.addFormDataPart("price", etPrice.getText().toString());
            builder.addFormDataPart("stockQuantity", etStockQuantity.getText().toString());
            builder.addFormDataPart("categoryId", String.valueOf(selectedCategory.getId()));
            if (selectedImageUri != null) {
                File imageFile = createTempFileFromUri(selectedImageUri);
                if (imageFile != null) {
                    builder.addFormDataPart(
                        "imageFile",
                        imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse(getContentResolver().getType(selectedImageUri)))
                    );
                }
            } else if (currentProduct != null && currentProduct.getImageUrl() != null) {
                builder.addFormDataPart("imageUrl", currentProduct.getImageUrl());
            }
            RequestBody requestBody = builder.build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                .url(API_URL + "/" + productId + "/with-image")
                .addHeader("Authorization", "Bearer " + token)
                .put(requestBody)
                .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(UpdateComponentActivity.this, "Error updating product: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(UpdateComponentActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(UpdateComponentActivity.this, "Error updating product: " + response.message(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            Log.e("UpdateComponentActivity", "Exception in updateProduct: " + e.getMessage(), e);
            Toast.makeText(this, "Error updating product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper to create a temp file from Uri for OkHttp upload
    private File createTempFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());
            FileOutputStream out = new FileOutputStream(tempFile);
            byte[] buf = new byte[4096];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    // Helper class to store product data
    private static class Product {
        private final int id;
        private final String name;
        private final String description;
        private final double price;
        private final int stockQuantity;
        private final String imageUrl;
        private final String companyName;

        public Product(int id, String name, String description, double price, int stockQuantity, String imageUrl, String companyName) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.stockQuantity = stockQuantity;
            this.imageUrl = imageUrl;
            this.companyName = companyName;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getPrice() { return price; }
        public int getStockQuantity() { return stockQuantity; }
        public String getImageUrl() { return imageUrl; }
        public String getCompanyName() { return companyName; }
    }
} 