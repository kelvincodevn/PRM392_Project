package com.example.pcbuilderguideapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity; // Or Fragment if applicable

public class CreateComponentActivity extends AppCompatActivity { // Change YourActivity to your actual activity name

    private TextView quantityTextView;
    private ImageView minusButton;
    private ImageView plusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_component); // Replace with your actual layout file name

        // 1. Find the views by their IDs
        quantityTextView = findViewById(R.id.quantity_text_view);
        minusButton = findViewById(R.id.minus_button);
        plusButton = findViewById(R.id.plus_button);

        // 2. Set OnClickListener for the minus button
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current quantity
                String currentQuantityStr = quantityTextView.getText().toString();
                int currentQuantity = Integer.parseInt(currentQuantityStr);

                // Decrease quantity if greater than 0
                if (currentQuantity > 0) {
                    currentQuantity--;
                    quantityTextView.setText(String.valueOf(currentQuantity));
                }
            }
        });

        // 3. Set OnClickListener for the plus button
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current quantity
                String currentQuantityStr = quantityTextView.getText().toString();
                int currentQuantity = Integer.parseInt(currentQuantityStr);

                // Increase quantity
                currentQuantity++;
                quantityTextView.setText(String.valueOf(currentQuantity));
            }
        });
    }
}