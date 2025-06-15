package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ImageView ivShop = findViewById(R.id.ivShop);
        ivShop.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, ShopActivity.class));
        });
        ImageView ivBuilder = findViewById(R.id.ivBuilder);
        ivBuilder.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, BuilderActivity.class));
        });
        ImageView ivSetting = findViewById(R.id.ivSetting);
        ivSetting.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, SettingActivity.class));
        });
        ImageView ivCatalog = findViewById(R.id.ivCatalog);
        ivCatalog.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, CatalogActivity.class));
        });
        ImageView ivHome = findViewById(R.id.ivHome);
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, HomeActivity.class));
        });
    }
}
