package com.example.pcbuilderguideapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pcbuilderguideapp.R;
import com.example.pcbuilderguideapp.models.Order;
import com.example.pcbuilderguideapp.models.OrderItem;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;
    private OnOrderClickListener listener;
    private boolean isStaffContext;
    private SimpleDateFormat dateFormat;

    public interface OnOrderClickListener {
        void onExpandClick(Order order, int position);
        void onCancelClick(Order order);
        void onConfirmClick(Order order);
    }

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this(orders, listener, false);
    }

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener, boolean isStaffContext) {
        this.orders = orders;
        this.listener = listener;
        this.isStaffContext = isStaffContext;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId;
        private TextView tvOrderDate;
        private TextView tvCustomerInfo;
        private TextView tvOrderItems;
        private TextView tvTotalAmount;
        private TextView tvOrderStatus;
        private TextView tvPaymentStatus;
        private ImageView btnExpand;
        private Button btnCancel, btnConfirm, btnCustomerCancel;
        private View expandedContent, staffActionsContainer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvCustomerInfo = itemView.findViewById(R.id.tvCustomerInfo);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            btnExpand = itemView.findViewById(R.id.btnExpand);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCustomerCancel = itemView.findViewById(R.id.btnCustomerCancel);
            staffActionsContainer = itemView.findViewById(R.id.staffActionsContainer);
            expandedContent = itemView.findViewById(R.id.expandedContent);
        }

        public void bind(Order order) {
            tvOrderId.setText(String.format("Order #%d", order.getOrderId()));
            tvOrderDate.setText(dateFormat.format(order.getOrderDate()));
            tvOrderStatus.setText(order.getOrderStatus());
            tvTotalAmount.setText(String.format("%.2f VND", order.getFinalAmount()));

            // Expanded content
            String customerInfo = String.format("Name: %s\nPhone: %s\nAddress: %s",
                    order.getCustomerName(),
                    order.getCustomerPhone(),
                    order.getShippingAddress());
            tvCustomerInfo.setText(customerInfo);

            StringBuilder itemsText = new StringBuilder();
            for (OrderItem item : order.getOrderItems()) {
                double total = item.getTotalPrice();
                if (total == 0) {
                    total = item.getUnitPrice() * item.getQuantity();
                }
                itemsText.append(String.format("• %s x%d @ %.2f VND each - %.2f VND\n",
                        item.getProductName(), item.getQuantity(), item.getUnitPrice(), total));
            }
            tvOrderItems.setText(itemsText.toString());

            // Payment status
            String paymentStatus = order.getPaymentStatus();
            if ("MoMo Payment".equalsIgnoreCase(order.getPaymentMethod()) && "Pending".equalsIgnoreCase(paymentStatus)
                && !"Cancelled".equalsIgnoreCase(order.getOrderStatus()) && !"Failed".equalsIgnoreCase(order.getOrderStatus())) {
                paymentStatus = "Paid";
            }
            tvPaymentStatus.setText(String.format("Payment Status: %s", paymentStatus));

            // Button visibility logic
            if (isStaffContext) {
                btnCustomerCancel.setVisibility(View.GONE);
                boolean canUpdate = "Pending".equalsIgnoreCase(order.getOrderStatus()) || "Processing".equalsIgnoreCase(order.getOrderStatus());
                staffActionsContainer.setVisibility(canUpdate ? View.VISIBLE : View.GONE);
            } else {
                staffActionsContainer.setVisibility(View.GONE);
                btnCustomerCancel.setVisibility("Pending".equalsIgnoreCase(order.getOrderStatus()) ? View.VISIBLE : View.GONE);
            }
            
            // Set click listeners
            btnConfirm.setOnClickListener(v -> {
                if (listener != null) listener.onConfirmClick(order);
            });
            btnCancel.setOnClickListener(v -> {
                if (listener != null) listener.onCancelClick(order);
            });
            btnCustomerCancel.setOnClickListener(v -> {
                if (listener != null) listener.onCancelClick(order);
            });

            btnExpand.setOnClickListener(v -> {
                boolean isExpanded = expandedContent.getVisibility() == View.VISIBLE;
                expandedContent.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
                btnExpand.setImageResource(isExpanded ? R.drawable.ic_plus_gradient : R.drawable.ic_minus_gradient);
                if (listener != null) {
                    listener.onExpandClick(order, getAdapterPosition());
                }
            });
        }
    }
} 