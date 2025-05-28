package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType; // Import for InputType
import android.widget.EditText; // Import for EditText
import android.widget.ImageView; // Import for ImageView
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Declare your EditText and ImageView for password toggle
    private EditText etPassword;
    private ImageView ivTogglePasswordVisibility;
    private boolean isPasswordVisible = false; // Flag to track password visibility

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Make sure this matches your XML file name

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views for password toggle
        etPassword = findViewById(R.id.etPassword);
        ivTogglePasswordVisibility = findViewById(R.id.ivTogglePasswordVisibility);

        // Set OnClickListener for the password toggle icon
        ivTogglePasswordVisibility.setOnClickListener(v -> {
            togglePasswordVisibility();
        });

        // Existing code for the "Log in here" TextView
        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Toggles the visibility of the password in the EditText.
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // If password is currently visible, hide it
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // Change the eye icon to 'closed eye' (original icon)
            ivTogglePasswordVisibility.setImageResource(R.drawable.icon_eye);
        } else {
            // If password is currently hidden, show it
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            // Change the eye icon to 'open eye' (the one with a slash)
            ivTogglePasswordVisibility.setImageResource(R.drawable.icon_eye_closed); // Ensure you have icon_eye_off drawable
        }
        // Move the cursor to the end of the text to maintain user experience
        etPassword.setSelection(etPassword.getText().length());
        // Invert the visibility state
        isPasswordVisible = !isPasswordVisible;
    }
}