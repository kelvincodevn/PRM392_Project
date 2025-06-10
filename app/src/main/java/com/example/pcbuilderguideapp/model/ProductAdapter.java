package com.example.pcbuilderguideapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.R;
import com.squareup.picasso.Picasso;
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
            .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        
        // Set product name
        holder.tvProductName.setText(product.getName());
        
        // Set price
        holder.tvProductPrice.setText(product.getPrice());
        
        // Set details (description and third party name)
        String details = product.getDescription();
        if (product.getThirdPartyName() != null && !product.getThirdPartyName().isEmpty()) {
            details += " • " + product.getThirdPartyName();
        }
        holder.tvProductDetails.setText(details);

        // Load image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get()
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_gpu_sample)
                .error(R.drawable.ic_gpu_sample)
                .into(holder.ivProductImage);
        } else if (product.getImageResId() != 0) {
            holder.ivProductImage.setImageResource(product.getImageResId());
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_gpu_sample);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvProductDetails;
        ProductViewHolder(View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDetails = itemView.findViewById(R.id.tvProductDetails);
        }
    }
} 