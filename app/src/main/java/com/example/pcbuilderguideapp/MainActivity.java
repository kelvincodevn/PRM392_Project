package com.example.pcbuilderguideapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText etUsername, etFullName, etEmail, etPhoneNumber, etPassword;
    private ImageView ivTogglePasswordVisibility;
    private Button btnSignUp;
    private CheckBox cbTerms;
    private boolean isPasswordVisible = false;
    private static final String REGISTER_URL = "https://pcpb-axhxcdckf8a5a5ed.southeastasia-01.azurewebsites.net/api/Auth/Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Trust all certificates for development
        trustAllCertificates();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePasswordVisibility = findViewById(R.id.ivTogglePasswordVisibility);
        btnSignUp = findViewById(R.id.btnSignUp);
        cbTerms = findViewById(R.id.cbTerms);

        // Set up password visibility toggle
        ivTogglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility());

        // Set up sign up button click listener
        btnSignUp.setOnClickListener(v -> performRegistration());

        // Set up login text click listener
        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
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

    private void performRegistration() {
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String address = ""; // Add address as empty string for now

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create JSON object for registration request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("fullName", fullName);
            jsonBody.put("email", email);
            jsonBody.put("phoneNumber", phoneNumber);
            jsonBody.put("password", password);
            jsonBody.put("address", address); // Add address to JSON body
            Log.d(TAG, "Registration attempt with username: " + username);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating JSON body: " + e.getMessage());
        }

        // Create request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create JSON request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                REGISTER_URL,
                jsonBody,
                response -> {
                    Log.d(TAG, "Đăng ký thành công. Response: " + response.toString());
                    Toast.makeText(MainActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    // Clear the input fields
                    etUsername.setText("");
                    etFullName.setText("");
                    etEmail.setText("");
                    etPhoneNumber.setText("");
                    etPassword.setText("");
                },
                error -> {
                    Log.e(TAG, "Đăng ký thất bại. Error: " + error.toString());
                    String errorMessage = "Đăng ký thất bại";
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
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
        );

        // Add request to queue
        requestQueue.add(jsonObjectRequest);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePasswordVisibility.setImageResource(R.drawable.icon_eye);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePasswordVisibility.setImageResource(R.drawable.icon_eye_closed);
        }
        etPassword.setSelection(etPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }
}