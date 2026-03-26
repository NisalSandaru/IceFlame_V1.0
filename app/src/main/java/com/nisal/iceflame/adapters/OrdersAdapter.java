package com.nisal.iceflame.adapters;

import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nisal.iceflame.R;
import com.nisal.iceflame.databinding.ItemOrderBinding;
import com.nisal.iceflame.model.OrderDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<OrderDto> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener{
        void onOrderClick(OrderDto order);
    }

    public OrdersAdapter(List<OrderDto> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderDto order = orders.get(position);

        // Order ID
        holder.binding.txtOrderId.setText("Order #" + order.getId());

        // Status
        String status = order.getStatus().toUpperCase();
        holder.binding.txtStatus.setText(status);

        // Date
        try {
            LocalDateTime ldt = LocalDateTime.parse(order.getCreatedAt());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy • h:mm a");
            holder.binding.txtDate.setText(ldt.format(formatter));
        } catch (Exception e) {
            holder.binding.txtDate.setText(order.getCreatedAt());
        }

        // Total
        holder.binding.txtTotal.setText("Rs. " + order.getTotalAmount());

        // 🔥 Progress + Color
        setupProgressBar(holder, status);

        holder.binding.getRoot().setOnClickListener(v -> {
            if(listener != null){
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;
        public ViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // =========================
    // 🔥 PROGRESS + ANIMATION
    // =========================
    private void setupProgressBar(ViewHolder holder, String status) {

        int progress = 0;
        String label = "";
        String colorHex = "#FF5722"; // default orange

        switch (status) {

            case "PENDING":
                progress = 10;
                label = "Waiting for confirmation ⏳";
                colorHex = "#FFC107"; // yellow
                break;

            case "CONFIRMED":
                progress = 30;
                label = "Order confirmed ✅";
                colorHex = "#03A9F4"; // blue
                break;

            case "PROCESSING":
                progress = 60;
                label = "Preparing your food 🍳";
                colorHex = "#FF5722"; // orange
                break;

            case "SHIPPED":
                progress = 85;
                label = "On the way 🚚";
                colorHex = "#9C27B0"; // purple
                break;

            case "DELIVERED":
                progress = 100;
                label = "Delivered 📦";
                colorHex = "#4CAF50"; // green
                break;

            case "CANCELLED":
                progress = 0;
                label = "Order cancelled ❌";
                colorHex = "#F44336"; // red
                break;
        }

        // Set label
        holder.binding.txtProgressLabel.setText(label);

        // 🔥 Animate Progress
        ObjectAnimator animation = ObjectAnimator.ofInt(
                holder.binding.orderProgress,
                "progress",
                0,
                progress
        );
        animation.setDuration(800);
        animation.start();

        // 🔥 Change Progress Color
        holder.binding.orderProgress.setProgressTintList(
                ColorStateList.valueOf(Color.parseColor(colorHex))
        );

        // 🔥 Change Status Badge Color
        holder.binding.txtStatus.setBackgroundTintList(
                ColorStateList.valueOf(Color.parseColor(colorHex))
        );
    }
}