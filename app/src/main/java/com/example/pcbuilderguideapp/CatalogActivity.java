package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CatalogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_product);
        ImageView ivShop = findViewById(R.id.ivShop);
        ivShop.setOnClickListener(v -> {
            startActivity(new Intent(CatalogActivity.this, ShopActivity.class));
        });
        ImageView ivBuilder = findViewById(R.id.ivBuilder);
        ivBuilder.setOnClickListener(v -> {
            startActivity(new Intent(CatalogActivity.this, BuilderActivity.class));
        });
        ImageView ivHome = findViewById(R.id.ivHome);
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(CatalogActivity.this, HomeActivity.class));
        });
        ImageView ivSetting = findViewById(R.id.ivSetting);
        ivSetting.setOnClickListener(v -> {
            startActivity(new Intent(CatalogActivity.this, SettingActivity.class));
        });
    }
}
