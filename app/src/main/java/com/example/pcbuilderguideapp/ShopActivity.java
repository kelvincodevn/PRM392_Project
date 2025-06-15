package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pcbuilderguideapp.model.CategoryAdapter;
import com.example.pcbuilderguideapp.model.ProductAdapter;
import com.example.pcbuilderguideapp.model.Product;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.text.DecimalFormat;
import android.widget.ImageView;

public class ShopActivity extends AppCompatActivity {
    private static final String TAG = "ShopActivity";
    private static final String API_URL = "https://10.0.2.2:7182/api/Product";
    private static final String SEARCH_API_URL = "https://10.0.2.2:7182/api/Product/search";
    private RecyclerView rvPopularProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Trust all certificates for development
        trustAllCertificates();

        // Initialize search EditText
        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    searchProducts(s.toString());
                } else {
                    fetchProducts(); // Reset to show all products when search is empty
                }
            }
        });

        // Cart icon navigation
        ImageView ivCart = findViewById(R.id.ivCart);
        ivCart.setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, CartActivity.class));
        });
        //Footer Navigation
        ImageView ivSetting = findViewById(R.id.ivSetting);
        ivSetting.setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, SettingActivity.class));
        });
        ImageView ivBuilder = findViewById(R.id.ivBuilder);
        ivBuilder.setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, BuilderActivity.class));
        });
        ImageView ivHome = findViewById(R.id.ivHome);
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, HomeActivity.class));
        });
        ImageView ivCatalog = findViewById(R.id.ivCatalog);
        ivCatalog.setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, CatalogActivity.class));
        });

        // Categories
        RecyclerView rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> categories = Arrays.asList(
            "CPU", "GPU", "Memory (RAM)", "Storage (SSD)",
            "Motherboard", "PSU", "Case", "Cooler", "Monitor", "Keyboard", "Mouse", "Headset", "Fan", "Cable", "OS"
        );
        rvCategories.setAdapter(new CategoryAdapter(categories));

        // Initialize Products RecyclerView
        rvPopularProducts = findViewById(R.id.rvPopularProducts);
        rvPopularProducts.setLayoutManager(new GridLayoutManager(this, 2));
        
        // Initialize product list and adapter
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        rvPopularProducts.setAdapter(productAdapter);

        // Fetch products from API
        fetchProducts();
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
            Log.e(TAG, "Error setting up SSL: " + e.getMessage());
        }
    }

    private String formatPrice(double price) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price) + "đ";
    }

    private void fetchProducts() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            API_URL,
            null,
            response -> {
                try {
                    Log.d(TAG, "API Response: " + response.toString());
                    productList.clear();
                    // Get the $values array from the response
                    JSONArray valuesArray = response.getJSONArray("$values");
                    for (int i = 0; i < valuesArray.length(); i++) {
                        JSONObject productJson = valuesArray.getJSONObject(i);
                        Log.d(TAG, "Product JSON: " + productJson.toString());
                        Product product = new Product();
                        // Try to get id, if not present use position as id
                        try {
                            product.setId(productJson.getInt("id"));
                        } catch (JSONException e) {
                            product.setId(i + 1); // Use position + 1 as id
                        }
                        product.setName(productJson.getString("productName"));
                        product.setDescription(productJson.optString("description", ""));
                        product.setPrice(formatPrice(productJson.getDouble("price")));
                        product.setStockQuantity(productJson.getInt("stockQuantity"));
                        product.setImageUrl(productJson.optString("imageUrl", ""));
                        product.setStatus(productJson.optString("status", ""));
                        product.setThirdPartyName(productJson.optString("companyName", ""));
                        productList.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    Toast.makeText(this, "Error loading products: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            },
            error -> {
                Log.e(TAG, "Error fetching products: " + error.getMessage());
                Toast.makeText(this, "Error loading products: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        );

        queue.add(jsonObjectRequest);
    }

    private void searchProducts(String query) {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            // URL encode the search query and use the correct parameter name 'productName'
            String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
            String searchUrl = SEARCH_API_URL + "?productName=" + encodedQuery;
            
            Log.d(TAG, "Searching with URL: " + searchUrl);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                searchUrl,
                null,
                response -> {
                    try {
                        Log.d(TAG, "Search API Response: " + response.toString());
                        productList.clear();
                        JSONArray valuesArray = response.getJSONArray("$values");
                        for (int i = 0; i < valuesArray.length(); i++) {
                            JSONObject productJson = valuesArray.getJSONObject(i);
                            Log.d(TAG, "Product JSON: " + productJson.toString());
                            Product product = new Product();
                            try {
                                product.setId(productJson.getInt("id"));
                            } catch (JSONException e) {
                                product.setId(i + 1);
                            }
                            product.setName(productJson.getString("productName"));
                            product.setDescription(productJson.optString("description", ""));
                            product.setPrice(formatPrice(productJson.getDouble("price")));
                            product.setStockQuantity(productJson.getInt("stockQuantity"));
                            product.setImageUrl(productJson.optString("imageUrl", ""));
                            product.setStatus(productJson.optString("status", ""));
                            product.setThirdPartyName(productJson.optString("companyName", ""));
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing search JSON: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing search results", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String errorMessage = "Error searching products: ";
                    if (error.networkResponse != null) {
                        errorMessage += "Status code: " + error.networkResponse.statusCode;
                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            errorMessage += ", Response: " + responseBody;
                        } catch (Exception e) {
                            errorMessage += ", Could not read response body";
                        }
                    } else {
                        errorMessage += error.getMessage();
                    }
                    Log.e(TAG, errorMessage);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            );

            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.e(TAG, "Error in searchProducts: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error performing search: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
} 