package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType; // Import for InputType
import android.widget.EditText; // Import for EditText
import android.widget.ImageView; // Import for ImageView
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    // Declare your EditText and ImageView for password toggle
    private EditText etPassword;
    private ImageView ivEye; // Matches the ID in your XML layout for the eye icon
    private boolean isPasswordVisible = false; // Flag to track password visibility state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in); // This line uses your specified layout file

        // Initialize views for password toggle
        etPassword = findViewById(R.id.etPassword);
        ivEye = findViewById(R.id.ivEye); // Find the ImageView by its ID

        // Set OnClickListener for the eye icon to toggle password visibility
        ivEye.setOnClickListener(v -> {
            togglePasswordVisibility();
        });

        // Existing code for the "Sign up" TextView
        TextView tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // You can add other view initializations and click listeners here
        // For example, for the login button:
        // Button btnLogin = findViewById(R.id.btnLogin);
        // btnLogin.setOnClickListener(v -> {
        //     // Handle login logic
        // });
    }

    /**
     * Toggles the visibility of the password in the EditText.
     * Changes the input type of the EditText and updates the eye icon accordingly.
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // If password is currently visible, hide it
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // Change the eye icon to 'closed eye' (original icon)
            ivEye.setImageResource(R.drawable.icon_eye);
        } else {
            // If password is currently hidden, show it
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            // Change the eye icon to 'open eye' (the one with a slash)
            ivEye.setImageResource(R.drawable.icon_eye_closed); // Make sure you have icon_eye_off drawable
        }
        // Move the cursor to the end of the text to maintain user experience
        etPassword.setSelection(etPassword.getText().length());
        // Invert the visibility state for the next toggle
        isPasswordVisible = !isPasswordVisible;
    }
}