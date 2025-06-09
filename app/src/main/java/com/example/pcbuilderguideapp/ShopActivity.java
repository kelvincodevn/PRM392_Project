package com.example.pcbuilderguideapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.model.CategoryAdapter;
import com.example.pcbuilderguideapp.model.ProductAdapter;
import com.example.pcbuilderguideapp.model.Product;
import java.util.Arrays;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Categories
        RecyclerView rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> categories = Arrays.asList(
            "CPU", "GPU", "Memory (RAM)", "Storage (SSD)",
            "Motherboard", "PSU", "Case", "Cooler", "Monitor", "Keyboard", "Mouse", "Headset", "Fan", "Cable", "OS"
        );
        rvCategories.setAdapter(new CategoryAdapter(categories));

        // Products
        RecyclerView rvProducts = findViewById(R.id.rvPopularProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        List<Product> products = Arrays.asList(
            new Product("AMD Ryzen 5 7600X", "$299.99", "6-Core Processor", R.drawable.cpu_img_1),
            new Product("AMD Ryzen 7 7800X3D", "$698.89", "8-Core Processor", R.drawable.cpu_img_1),
            new Product("GeForce RTX 3070 Ti", "$1089.99", "16GB GDDR6X", R.drawable.cpu_img_1),
            new Product("ASUS GeForce RTX 4060", "$429.99", "8GB GDDR6", R.drawable.cpu_img_1)
        );
        rvProducts.setAdapter(new ProductAdapter(products));
    }
} 