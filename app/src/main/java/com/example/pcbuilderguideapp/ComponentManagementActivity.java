package com.example.pcbuilderguideapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class ComponentManagementActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_management);

        // Find the Add Component button
        FrameLayout addComponentButton = findViewById(R.id.add_component_button);
        addComponentButton.setOnClickListener(v -> {
            Intent intent = new Intent(ComponentManagementActivity.this, CreateComponentActivity.class);
            startActivity(intent);
        });

        // Find the List Component button
        FrameLayout listComponentButton = findViewById(R.id.list_component_button);
        listComponentButton.setOnClickListener(v -> {
            Intent intent = new Intent(ComponentManagementActivity.this, ComponentListActivity.class);
            startActivity(intent);
        });
    }
}
