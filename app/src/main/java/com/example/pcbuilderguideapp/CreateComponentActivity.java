package com.example.pcbuilderguideapp;

import android.os.Bundle;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_component);

        // Trust all certificates for development
        trustAllCertificates();

        // Initialize views
        initializeViews();

        // Set up back button
        ImageView backButton = findViewById(R.id.ivBack);
        backButton.setOnClickListener(v -> finish());

        // Set up confirm button
        btnConfirm.setOnClickListener(v -> createProduct());

        // Set up cancel button
        btnCancel.setOnClickListener(v -> finish());

        // Load categories
        loadCategories();
    }

    private void initializeViews() {
        etProductName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etStockQuantity = findViewById(R.id.etStockQuantity);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadCategories() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET,
            CATEGORY_API_URL,
            null,
            response -> {
                try {
                    categories.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject categoryJson = response.getJSONObject(i);
                        Category category = new Category(
                            categoryJson.getInt("categoryId"),
                            categoryJson.getString("categoryName")
                        );
                        categories.add(category);
                    }
                    updateCategorySpinner();
                } catch (JSONException e) {
                    Toast.makeText(CreateComponentActivity.this, "Error parsing categories: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Toast.makeText(CreateComponentActivity.this, "Error loading categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );

        queue.add(request);
    }

    private void updateCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void createProduct() {
        try {
            // Get selected category
            int selectedPosition = spinnerCategory.getSelectedItemPosition();
            if (selectedPosition < 0 || selectedPosition >= categories.size()) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }
            Category selectedCategory = categories.get(selectedPosition);

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
            );

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