package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Footer Navigation
        ImageView ivShop = findViewById(R.id.ivShop);
        ivShop.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, ShopActivity.class));
        });
        ImageView ivBuilder = findViewById(R.id.ivBuilder);
        ivBuilder.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, BuilderActivity.class));
        });
        ImageView ivHome = findViewById(R.id.ivHome);
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, HomeActivity.class));
        });
        ImageView ivCatalog = findViewById(R.id.ivCatalog);
        ivCatalog.setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, CatalogActivity.class));
        });
    }
}
