package com.og.pcbuilderguideapp.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.og.pcbuilderguideapp.R;
import com.og.pcbuilderguideapp.ShopDetailActivity;
import com.og.pcbuilderguideapp.models.Product;
import com.bumptech.glide.Glide;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<Product> products;
    private final Context context;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_shop_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        
        // Set product name
        holder.tvProductName.setText(product.getName());
        
        // Set price
        holder.tvProductPrice.setText(String.format("%,.2f VND", product.getPrice()));

        // Set company name
        holder.tvCompanyName.setText(product.getCompanyName());

        // Set stock quantity
        holder.tvStockQuantity.setText("In Stock: " + product.getQuantity());
        
        // Set description
        holder.tvProductDetails.setText(product.getDescription());

        // Load image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            android.util.Log.d("ProductAdapter", "Loading image URL: " + product.getImageUrl());
            Glide.with(holder.ivProductImage.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_gpu_sample)
                .error(R.drawable.ic_gpu_sample)
                .into(holder.ivProductImage);
        } else {
            android.util.Log.d("ProductAdapter", "No image URL for product: " + product.getName());
            holder.ivProductImage.setImageResource(R.drawable.ic_gpu_sample);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShopDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            intent.putExtra("product_name", product.getName());
            intent.putExtra("product_price", product.getPrice());
            intent.putExtra("product_description", product.getDescription());
            intent.putExtra("product_company", product.getCompanyName());
            intent.putExtra("product_stock", product.getQuantity());
            intent.putExtra("product_image_url", product.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvProductDetails, tvCompanyName, tvStockQuantity;
        ProductViewHolder(View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDetails = itemView.findViewById(R.id.tvProductDetails);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvStockQuantity = itemView.findViewById(R.id.tvStockQuantity);
        }
    }
} 