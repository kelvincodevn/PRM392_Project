package com.example.pcbuilderguideapp;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ComponentManagementActivity extends AppCompatActivity {
    private static final String TAG = "ComponentManagementActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_management);

        try {
            // Find the Add Component button
            FrameLayout addComponentButton = findViewById(R.id.add_component_button);
            if (addComponentButton != null) {
                addComponentButton.setOnClickListener(v -> {
                    try {
                        Log.d(TAG, "Add Component button clicked");
                        Intent intent = new Intent(ComponentManagementActivity.this, CreateComponentActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting CreateComponentActivity: " + e.getMessage(), e);
                        Toast.makeText(this, "Error opening Add Component: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e(TAG, "Add Component button not found in layout");
            }

            // Find the List Component button
            FrameLayout listComponentButton = findViewById(R.id.list_component_button);
            if (listComponentButton != null) {
                listComponentButton.setOnClickListener(v -> {
                    try {
                        Log.d(TAG, "List Component button clicked");
                        Intent intent = new Intent(ComponentManagementActivity.this, ComponentListActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting ComponentListActivity: " + e.getMessage(), e);
                        Toast.makeText(this, "Error opening List Component: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e(TAG, "List Component button not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing component management: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
