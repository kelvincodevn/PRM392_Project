package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";
    
    // UI Components
    private EditText etEmail, etNewPassword, etConfirmPassword;
    private ImageView ivEyeNewPassword, ivEyeConfirmPassword;
    private Button btnResetPassword;
    private TextView tvBackToLogin;
    
    // Password visibility flags
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        // Initialize views
        initializeViews();
        
        // Set up click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivEyeNewPassword = findViewById(R.id.ivEyeNewPassword);
        ivEyeConfirmPassword = findViewById(R.id.ivEyeConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void setupClickListeners() {
        // Password visibility toggles
        ivEyeNewPassword.setOnClickListener(v -> toggleNewPasswordVisibility());
        ivEyeConfirmPassword.setOnClickListener(v -> toggleConfirmPasswordVisibility());
        
        // Reset password button
        btnResetPassword.setOnClickListener(v -> performPasswordReset());
        
        // Back to login
        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void performPasswordReset() {
        String email = etEmail.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            etNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return;
        }

        // TODO: Implement API call for password reset
        // For now, just show a success message
        Toast.makeText(this, "Password reset request sent to " + email, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Password reset requested for email: " + email);
        
        // Navigate back to sign in
        Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void toggleNewPasswordVisibility() {
        if (isNewPasswordVisible) {
            // Hide password
            etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivEyeNewPassword.setImageResource(R.drawable.icon_eye);
        } else {
            // Show password
            etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivEyeNewPassword.setImageResource(R.drawable.icon_eye_closed);
        }
        etNewPassword.setSelection(etNewPassword.getText().length());
        isNewPasswordVisible = !isNewPasswordVisible;
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            // Hide password
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivEyeConfirmPassword.setImageResource(R.drawable.icon_eye);
        } else {
            // Show password
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivEyeConfirmPassword.setImageResource(R.drawable.icon_eye_closed);
        }
        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
    }
} 