package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.pcbuilderguideapp.utils.TokenManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class ComponentListActivity extends AppCompatActivity {
    private static final String API_URL = "https://10.0.2.2:7182/api/Product";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> products = new ArrayList<>();
    private EditText etSearch;
    private ImageView ivFilter;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_list);

        try {
            trustAllCertificates();
            initializeViews();

            ImageView backButton = findViewById(R.id.ivBack);
            if (backButton != null) {
                backButton.setOnClickListener(v -> finish());
            }

            if (etSearch != null) {
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0) {
                            fetchProducts(API_URL + "/search?productName=" + s.toString());
                        } else {
                            fetchProducts(API_URL);
                        }
                    }
                });
            }

            if (ivFilter != null) {
                ivFilter.setOnClickListener(v -> Toast.makeText(this, "Filter functionality coming soon", Toast.LENGTH_SHORT).show());
            }

            fetchProducts(API_URL);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing component list: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the product list when returning from update activity
        fetchProducts(API_URL);
    }

    private void initializeViews() {
        try {
            recyclerView = findViewById(R.id.recyclerView);
            etSearch = findViewById(R.id.etSearch);
            ivFilter = findViewById(R.id.ivFilter);
            progressBar = findViewById(R.id.progressBar);
            tvEmpty = findViewById(R.id.tvEmpty);
            
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new ProductAdapter(products);
                recyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void fetchProducts(String url) {
        showLoading(true);
        
        // Get authentication token
        String token = TokenManager.getInstance(this).getToken();
        if (token == null) {
            showError("Authentication required. Please login again.");
            return;
        }
        
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    Log.d("ComponentListActivity", "API Response: " + response.toString());
                    products.clear();
                    // Get the $values array from the response
                    JSONArray valuesArray = response.getJSONArray("$values");
                    for (int i = 0; i < valuesArray.length(); i++) {
                        JSONObject productJson = valuesArray.getJSONObject(i);
                        Log.d("ComponentListActivity", "Product JSON: " + productJson.toString());
                        
                        // Get productId from the JSON
                        int productId = productJson.getInt("productId");
                        
                        Product product = new Product(
                            productId,
                            productJson.getString("productName"),
                            productJson.optString("description", ""),
                            productJson.getDouble("price"),
                            productJson.getInt("stockQuantity"),
                            productJson.optString("imageUrl", ""),
                            productJson.optString("companyName", "")
                        );
                        products.add(product);
                    }
                    updateProductList();
                } catch (JSONException e) {
                    Log.e("ComponentListActivity", "Error parsing products: " + e.getMessage(), e);
                    showError("Error parsing products: " + e.getMessage());
                }
            },
            error -> {
                Log.e("ComponentListActivity", "Error loading products: " + error.getMessage(), error);
                showError("Error loading products: " + error.getMessage());
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

    private void updateProductList() {
        showLoading(false);
        Log.d("ComponentListActivity", "updateProductList called. Product count: " + products.size());
        for (Product p : products) {
            Log.d("ComponentListActivity", "Product in list: id=" + p.getId() + ", name=" + p.getName());
        }
        if (products.isEmpty()) {
            tvEmpty.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            tvEmpty.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? ProgressBar.VISIBLE : ProgressBar.GONE);
        recyclerView.setVisibility(loading ? RecyclerView.GONE : RecyclerView.VISIBLE);
        tvEmpty.setVisibility(TextView.GONE);
    }

    private void showError(String message) {
        showLoading(false);
        products.clear();
        adapter.notifyDataSetChanged();
        tvEmpty.setText(message);
        tvEmpty.setVisibility(TextView.VISIBLE);
    }

    private void showDeleteConfirmation(Product product) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete '" + product.getName() + "'?")
            .setPositiveButton("Delete", (dialog, which) -> deleteProduct(product.getId()))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteProduct(int productId) {
        String token = TokenManager.getInstance(this).getToken();
        if (token == null) {
            Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.DELETE,
            API_URL + "/" + productId,
            null,
            response -> {
                Toast.makeText(ComponentListActivity.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                // Refresh the product list
                fetchProducts(API_URL);
            },
            error -> {
                Log.e("ComponentListActivity", "Error deleting product: " + error.getMessage(), error);
                Toast.makeText(ComponentListActivity.this, "Error deleting product: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) { return true; }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static class Product {
        private final int id;
        private final String name;
        private final String description;
        private final double price;
        private final int stockQuantity;
        private final String imageUrl;
        private final String companyName;
        public Product(int id, String name, String description, double price, int stockQuantity, String imageUrl, String companyName) {
            this.id = id; this.name = name; this.description = description; this.price = price; this.stockQuantity = stockQuantity; this.imageUrl = imageUrl; this.companyName = companyName;
        }
        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getPrice() { return price; }
        public int getStockQuantity() { return stockQuantity; }
        public String getImageUrl() { return imageUrl; }
        public String getCompanyName() { return companyName; }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private final List<Product> products;
        public ProductAdapter(List<Product> products) { this.products = products; }
        @Override
        public ProductViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = getLayoutInflater().inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            Product product = products.get(position);
            holder.tvProductName.setText(product.getName());
            holder.tvProductDetails.setText(product.getDescription());
            holder.tvProductPrice.setText(String.format("$%.2f", product.getPrice()));
            holder.tvStockQuantity.setText("In Stock: " + product.getStockQuantity());
            holder.tvCompanyName.setText(product.getCompanyName());
            
            if (!product.getImageUrl().isEmpty()) {
                Glide.with(holder.ivProductImage.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_gpu_sample)
                        .into(holder.ivProductImage);
            } else {
                holder.ivProductImage.setImageResource(R.drawable.ic_gpu_sample);
            }

            // Set up update button click listener
            holder.btnUpdate.setOnClickListener(v -> {
                Intent intent = new Intent(ComponentListActivity.this, UpdateComponentActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            });

            // Set up delete button click listener
            holder.btnDelete.setOnClickListener(v -> {
                showDeleteConfirmation(product);
            });
        }
        @Override
        public int getItemCount() { return products.size(); }
        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView tvProductName, tvProductPrice, tvProductDetails, tvCompanyName, tvStockQuantity;
            ImageView ivProductImage;
            Button btnUpdate, btnDelete;
            public ProductViewHolder(android.view.View itemView) {
                super(itemView);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
                tvProductDetails = itemView.findViewById(R.id.tvProductDetails);
                tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
                tvStockQuantity = itemView.findViewById(R.id.tvStockQuantity);
                ivProductImage = itemView.findViewById(R.id.ivProductImage);
                btnUpdate = itemView.findViewById(R.id.btnUpdate);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
} 