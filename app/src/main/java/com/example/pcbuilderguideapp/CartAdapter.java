package com.example.pcbuilderguideapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.models.CartItem;
import com.squareup.picasso.Picasso;
import java.util.List;
import com.example.pcbuilderguideapp.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private boolean isPaymentContext = false;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public CartAdapter(List<CartItem> cartItems, boolean isPaymentContext) {
        this.cartItems = cartItems;
        this.isPaymentContext = isPaymentContext;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        if (item.getProduct() != null) {
            holder.tvProductName.setText(item.getProduct().getName());
            holder.tvCompanyName.setText(item.getProduct().getCompanyName());
            holder.tvProductPrice.setText("$" + String.format("%.2f", item.getProduct().getPrice()));
            if (item.getProduct().getImageUrl() != null && !item.getProduct().getImageUrl().isEmpty()) {
                Picasso.get().load(item.getProduct().getImageUrl()).into(holder.ivProductImage);
            } else {
                holder.ivProductImage.setImageResource(R.drawable.cpu_img_1);
            }
        }
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        int maxQuantity = item.getProduct() != null ? item.getProduct().getQuantity() : Integer.MAX_VALUE;
        holder.btnDecreaseQuantity.setOnClickListener(v -> {
            int current = item.getQuantity();
            if (current > 1) {
                if (isPaymentContext) {
                    item.setQuantity(current - 1);
                    holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                    if (holder.itemView.getContext() instanceof PaymentActivity) {
                        ((PaymentActivity) holder.itemView.getContext()).updateTotalPrice();
                    }
                } else {
                    updateQuantity(holder, item, current - 1, maxQuantity);
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "Quantity must be at least 1", Toast.LENGTH_SHORT).show();
            }
        });
        holder.btnIncreaseQuantity.setOnClickListener(v -> {
            int current = item.getQuantity();
            if (current < maxQuantity) {
                if (isPaymentContext) {
                    item.setQuantity(current + 1);
                    holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                    if (holder.itemView.getContext() instanceof PaymentActivity) {
                        ((PaymentActivity) holder.itemView.getContext()).updateTotalPrice();
                    }
                } else {
                    updateQuantity(holder, item, current + 1, maxQuantity);
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "Maximum stock reached", Toast.LENGTH_SHORT).show();
            }
        });

        if (isPaymentContext) {
            holder.btnDeleteItem.setVisibility(View.GONE);
        } else {
            holder.btnDeleteItem.setVisibility(View.VISIBLE);
            holder.btnDeleteItem.setOnClickListener(v -> {
                RetrofitClient.getInstance(holder.itemView.getContext())
                    .getApiService()
                    .deleteCartItem(item.getCartItemId())
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                int pos = holder.getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    cartItems.remove(pos);
                                    notifyItemRemoved(pos);
                                    if (holder.itemView.getContext() instanceof CartActivity) {
                                        ((CartActivity) holder.itemView.getContext()).updateTotalPrice();
                                    }
                                }
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            });
        }
    }

    private void updateQuantity(CartViewHolder holder, CartItem item, int newQuantity, int maxQuantity) {
        RetrofitClient.getInstance(holder.itemView.getContext())
            .getApiService()
            .updateCartItem(item.getCartItemId(), newQuantity)
            .enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        item.setQuantity(newQuantity);
                        holder.tvQuantity.setText(String.valueOf(newQuantity));
                        // Optionally notify parent to update total price
                        if (holder.itemView.getContext() instanceof CartActivity) {
                            ((CartActivity) holder.itemView.getContext()).updateTotalPrice();
                        }
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(holder.itemView.getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvCompanyName, tvProductPrice, tvQuantity;
        ImageView btnDecreaseQuantity, btnIncreaseQuantity, btnDeleteItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecreaseQuantity = itemView.findViewById(R.id.btnDecreaseQuantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btnIncreaseQuantity);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
        }
    }
} 