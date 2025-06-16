package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class BuilderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_it_way);
        ImageView ivShop = findViewById(R.id.ivShop);
        ivShop.setOnClickListener(v -> {
            startActivity(new Intent(BuilderActivity.this, ShopActivity.class));
        });
        ImageView ivSetting = findViewById(R.id.ivSetting);
        ivSetting.setOnClickListener(v -> {
            startActivity(new Intent(BuilderActivity.this, SettingActivity.class));
        });
        ImageView ivHome = findViewById(R.id.ivHome);
        ivHome.setOnClickListener(v -> {
            startActivity(new Intent(BuilderActivity.this, HomeActivity.class));
        });
        ImageView ivCatalog = findViewById(R.id.ivCatalog);
        ivCatalog.setOnClickListener(v -> {
            startActivity(new Intent(BuilderActivity.this, CatalogActivity.class));
        });
    }
}
