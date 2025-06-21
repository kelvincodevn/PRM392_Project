package com.example.pcbuilderguideapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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

public class ComponentListActivity extends AppCompatActivity {
    private static final String API_URL = "https://pcpb-axhxcdckf8a5a5ed.southeastasia-01.azurewebsites.net/api/auth/login";
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

        trustAllCertificates();
        initializeViews();

        ImageView backButton = findViewById(R.id.ivBack);
        backButton.setOnClickListener(v -> finish());

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

        ivFilter.setOnClickListener(v -> Toast.makeText(this, "Filter functionality coming soon", Toast.LENGTH_SHORT).show());

        fetchProducts(API_URL);
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        etSearch = findViewById(R.id.etSearch);
        ivFilter = findViewById(R.id.ivFilter);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(products);
        recyclerView.setAdapter(adapter);
    }

    private void fetchProducts(String url) {
        showLoading(true);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    products.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject productJson = response.getJSONObject(i);
                        Product product = new Product(
                            productJson.getInt("productId"),
                            productJson.getString("productName"),
                            productJson.getString("description"),
                            productJson.getDouble("price"),
                            productJson.getInt("stockQuantity"),
                            productJson.optString("imageUrl", ""),
                            productJson.optString("companyName", "")
                        );
                        products.add(product);
                    }
                    updateProductList();
                } catch (JSONException e) {
                    showError("Error parsing products: " + e.getMessage());
                }
            },
            error -> showError("Error loading products: " + error.getMessage())
        );
        queue.add(request);
    }

    private void updateProductList() {
        showLoading(false);
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
            holder.tvDescription.setText(product.getDescription());
            holder.tvPrice.setText(String.format("$%.2f", product.getPrice()));
            holder.tvQuantity.setText(String.valueOf(product.getStockQuantity()));
            if (!product.getImageUrl().isEmpty()) {
                Glide.with(holder.ivProductImage.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_gpu_sample)
                        .into(holder.ivProductImage);
            } else {
                holder.ivProductImage.setImageResource(R.drawable.ic_gpu_sample);
            }
        }
        @Override
        public int getItemCount() { return products.size(); }
        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView tvProductName, tvDescription, tvPrice, tvQuantity;
            ImageView ivProductImage;
            public ProductViewHolder(android.view.View itemView) {
                super(itemView);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvProductName = itemView.findViewById(R.id.tvProductName);                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                ivProductImage = itemView.findViewById(R.id.ivProductImage);
            }
        }
    }
} 