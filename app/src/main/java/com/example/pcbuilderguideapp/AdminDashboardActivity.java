package com.example.pcbuilderguideapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView dropdownText;
    private View dropdownButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_general_dashboard);
        setupNavigation();
    }

    private void setupNavigation() {
        // Find the appropriate dropdown elements based on current view
        dropdownText = findViewById(R.id.dropdown_text);
        dropdownButton = findViewById(R.id.dropdown_container);
        
        if (dropdownText == null) {
            dropdownText = findViewById(R.id.approval_dropdown_text);
            dropdownButton = findViewById(R.id.approval_dropdown_container);
        }

        if (dropdownButton != null) {
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
                    setContentView(R.layout.admin_general_dashboard);
                    setupNavigation();
                    popupWindow.dismiss();
                });
                popupView.findViewById(R.id.item_management).setOnClickListener(item -> {
                    // Handle Management tab click
                    setContentView(R.layout.admin_management_staff);
                    setupNavigation();
                    popupWindow.dismiss();
                });
                popupView.findViewById(R.id.item_approval).setOnClickListener(item -> {
                    // Handle Approval tab click
                    setContentView(R.layout.admin_approval_request);
                    setupNavigation();
                    popupWindow.dismiss();
                });

                // Show the popup below the dropdown button
                popupWindow.showAsDropDown(dropdownButton, 0, 0);
            });
        }

        // Set up tab navigation for management staff view
        View thirdPartyTab = findViewById(R.id.tab_third_parties);
        if (thirdPartyTab != null) {
            thirdPartyTab.setOnClickListener(v -> {
                setContentView(R.layout.admin_management_thirdparty);
                setupNavigation();
            });
        }

        // Set up tab navigation for management third party view
        View staffListTab = findViewById(R.id.tab_staff_list);
        if (staffListTab != null) {
            staffListTab.setOnClickListener(v -> {
                setContentView(R.layout.admin_management_staff);
                setupNavigation();
            });
        }

        // Set up tab navigation for approval request view
        View othersTab = findViewById(R.id.tab_others);
        if (othersTab != null) {
            othersTab.setOnClickListener(v -> {
                setContentView(R.layout.admin_approval_others);
                setupNavigation();
            });
        }

        // Set up tab navigation for approval others view
        View requestedByStaffsTab = findViewById(R.id.tab_requested_by_staffs);
        if (requestedByStaffsTab != null) {
            requestedByStaffsTab.setOnClickListener(v -> {
                setContentView(R.layout.admin_approval_request);
                setupNavigation();
            });
        }
    }
}
