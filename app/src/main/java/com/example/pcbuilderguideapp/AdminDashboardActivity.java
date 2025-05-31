package com.example.pcbuilderguideapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_general_dashboard);

        View dropdownButton = findViewById(R.id.dropdown_container);
        dropdownButton.setOnClickListener(v -> {
            View popupView = LayoutInflater.from(this).inflate(R.layout.dropdown_menu, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true);

            // Optional: set elevation for shadow
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.setElevation(10);
            }

            // Set click listeners for each item
            popupView.findViewById(R.id.item_general).setOnClickListener(item -> {
                // Handle General statistics click
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.item_management).setOnClickListener(item -> {
                // Handle Management tab click
                popupWindow.dismiss();
            });
            popupView.findViewById(R.id.item_approval).setOnClickListener(item -> {
                // Handle Approval tab click
                popupWindow.dismiss();
            });

            // Show the popup below the dropdown button
            popupWindow.showAsDropDown(dropdownButton, 0, 0);
        });
    }
}
