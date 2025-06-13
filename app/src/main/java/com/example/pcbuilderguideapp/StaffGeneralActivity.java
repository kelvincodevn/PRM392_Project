package com.example.pcbuilderguideapp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class StaffGeneralActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_general);

        View tabOnDeliveryGradient = findViewById(R.id.tabOnDeliveryGradient);
        View tabOnDeliveryPlain = findViewById(R.id.tabOnDeliveryPlain);
        View tabHistoryGradient = findViewById(R.id.tabHistoryGradient);
        View tabHistoryPlain = findViewById(R.id.tabHistoryPlain);
        View layoutOnDelivery = findViewById(R.id.layoutOnDelivery);
        View layoutHistory = findViewById(R.id.layoutHistory);

        View.OnClickListener onDeliveryClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutOnDelivery.setVisibility(View.VISIBLE);
                layoutHistory.setVisibility(View.GONE);
                tabOnDeliveryGradient.setVisibility(View.VISIBLE);
                tabOnDeliveryPlain.setVisibility(View.GONE);
                tabHistoryGradient.setVisibility(View.GONE);
                tabHistoryPlain.setVisibility(View.VISIBLE);
            }
        };
        View.OnClickListener historyClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutOnDelivery.setVisibility(View.GONE);
                layoutHistory.setVisibility(View.VISIBLE);
                tabOnDeliveryGradient.setVisibility(View.GONE);
                tabOnDeliveryPlain.setVisibility(View.VISIBLE);
                tabHistoryGradient.setVisibility(View.VISIBLE);
                tabHistoryPlain.setVisibility(View.GONE);
            }
        };
        tabOnDeliveryGradient.setOnClickListener(onDeliveryClick);
        tabOnDeliveryPlain.setOnClickListener(onDeliveryClick);
        tabHistoryGradient.setOnClickListener(historyClick);
        tabHistoryPlain.setOnClickListener(historyClick);
    }
} 