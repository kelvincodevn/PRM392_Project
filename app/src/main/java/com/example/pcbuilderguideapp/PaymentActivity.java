package com.example.pcbuilderguideapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.models.CartItem;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView rvPaymentCartItems;
    private TextView tvTotalPaymentPrice, tvPaymentType;
    private EditText etAddress;
    private Button btnCreateOrder, btnCancelOrder;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        rvPaymentCartItems = findViewById(R.id.rvPaymentCartItems);
        tvTotalPaymentPrice = findViewById(R.id.tvTotalPaymentPrice);
        tvPaymentType = findViewById(R.id.tvPaymentType);
        etAddress = findViewById(R.id.etAddress);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Get cart items from intent
        Intent intent = getIntent();
        ArrayList<CartItem> items = (ArrayList<CartItem>) intent.getSerializableExtra("cart_items");
        if (items != null) {
            cartItems.addAll(items);
        }

        cartAdapter = new CartAdapter(cartItems, true);
        rvPaymentCartItems.setAdapter(cartAdapter);
        rvPaymentCartItems.setLayoutManager(new LinearLayoutManager(this));

        updateTotalPrice();

        btnCreateOrder.setOnClickListener(v -> {
            String address = etAddress.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Implement order creation logic (API call)
            Toast.makeText(this, "Order created!", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnCancelOrder.setOnClickListener(v -> finish());
    }

    public void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.getProduct() != null) {
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }
        tvTotalPaymentPrice.setText("Total price $" + String.format("%.2f", total));
    }
} 