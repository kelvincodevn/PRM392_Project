package com.og.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType; // Import for InputType
import android.util.Log;
import android.widget.Button;
import android.widget.EditText; // Import for EditText
import android.widget.ImageView; // Import for ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.og.pcbuilderguideapp.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    // Declare your EditText and ImageView for password toggle
    private EditText etUsername, etPassword;
    private ImageView ivEye; // Matches the ID in your XML layout for the eye icon
    private Button btnLogin;
    private boolean isPasswordVisible = false; // Flag to track password visibility state
    // Change localhost to 10.0.2.2 for Android Emulator
    private static final String LOGIN_URL = "https://pcpb-axhxcdckf8a5a5ed.southeastasia-01.azurewebsites.net/api/Auth/Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in); // This line uses your specified layout file

        // Trust all certificates for development
        trustAllCertificates();

        // Initialize views for password toggle
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivEye = findViewById(R.id.ivEye); // Find the ImageView by its ID
        btnLogin = findViewById(R.id.btnLogin);

        // Set OnClickListener for the eye icon to toggle password visibility
        ivEye.setOnClickListener(v -> {
            togglePasswordVisibility();
        });

        // Set up login button click listener
        btnLogin.setOnClickListener(v -> performLogin());

        // Existing code for the "Sign up" TextView
        TextView tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Add click listener for "Forgot your password?" TextView
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // You can add other view initializations and click listeners here
        // For example, for the login button:
        // Button btnLogin = findViewById(R.id.btnLogin);
        // btnLogin.setOnClickListener(v -> {
        //     // Handle login logic
        // });
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up SSL: " + e.getMessage());
        }
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create JSON object for login request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            Log.d(TAG, "Login attempt with username: " + username);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating JSON body: " + e.getMessage());
        }

        // Create request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create JSON request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_URL,
                jsonBody,
                response -> {
                    Log.d(TAG, "Đăng nhập thành công. Response: " + response.toString());
                    Toast.makeText(SignInActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    
                    try {
                        // Get user role and token from response
                        String role = response.getString("role");
                        String token = response.getString("token");
                        int userId = response.getInt("userId");
                        
                        // Get optional fields with null checks
                        String fullName = response.has("fullName") && !response.isNull("fullName") 
                            ? response.getString("fullName") 
                            : "";
                        String phoneNumber = response.has("phoneNumber") && !response.isNull("phoneNumber") 
                            ? response.getString("phoneNumber") 
                            : "";
                        
                        // Save token and user info
                        TokenManager.getInstance(SignInActivity.this).saveToken(token);
                        TokenManager.getInstance(SignInActivity.this).saveUserId(userId);
                        TokenManager.getInstance(SignInActivity.this).saveFullName(fullName);
                        TokenManager.getInstance(SignInActivity.this).savePhoneNumber(phoneNumber);
                        
                        Log.d(TAG, "Login successful - Token: " + token);
                        Log.d(TAG, "User ID: " + userId);
                        Log.d(TAG, "Role: " + role);
                        Log.d(TAG, "Full Name: " + fullName);
                        Log.d(TAG, "Phone Number: " + phoneNumber);
                        
                        Intent intent;
                        
                        // Route to appropriate activity based on role
                        switch (role.toLowerCase()) {
                            case "customer":
                                intent = new Intent(SignInActivity.this, HomeActivity.class);
                                break;
                            case "admin":
                                intent = new Intent(SignInActivity.this, AdminDashboardActivity.class);
                                break;
                            case "third party":
                                intent = new Intent(SignInActivity.this, ComponentManagementActivity.class);
                                break;
                            case "staff":
                                intent = new Intent(SignInActivity.this, StaffGeneralActivity.class);
                                break;
                            default:
                                Toast.makeText(SignInActivity.this, "Unknown user role: " + role, Toast.LENGTH_SHORT).show();
                                return;
                        }
                        
                        // Start the appropriate activity
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing login response: " + e.getMessage());
                        Log.e(TAG, "Response content: " + response.toString());
                        Toast.makeText(SignInActivity.this, "Error processing login response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Login failed. Error: " + error.toString());
                    String errorMessage = "Login failed";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorResponse = new String(error.networkResponse.data);
                            Log.e(TAG, "Error response: " + errorResponse);
                            JSONObject errorJson = new JSONObject(errorResponse);
                            if (errorJson.has("message")) {
                                errorMessage = errorJson.getString("message");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error response: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
        );

        // Add request to queue
        requestQueue.add(jsonObjectRequest);
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