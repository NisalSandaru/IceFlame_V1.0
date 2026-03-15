package com.nisal.iceflame.adapters;

import android.view.LayoutInflater;
import android.view.View;
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

    public OrdersAdapter(List<OrderDto> orders) {
        this.orders = orders;
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
        View itemView = holder.itemView;
        android.content.Context context = itemView.getContext();

        holder.binding.txtOrderId.setText("Order #"+order.getId());

        // --- Status Text & Color ---
        String status = order.getStatus().toUpperCase();
        holder.binding.txtStatus.setText(getStatusIcon(status) + " " + status);
        setStatusColor(holder, status, context);

        // --- Date Formatting ---
        try {
            String dateTime = order.getCreatedAt();
            LocalDateTime ldt = LocalDateTime.parse(dateTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy • h:mm a");
            holder.binding.txtDate.setText(ldt.format(formatter));
        } catch (Exception e) {
            holder.binding.txtDate.setText(order.getCreatedAt());
        }

        // --- Total Price ---
        holder.binding.txtTotal.setText("Total: Rs. " + order.getTotalAmount());

        // --- Progress Stepper UI ---
        setupProgressStepper(holder, status, context);

        holder.binding.orderCard.setOnClickListener(v->{

        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // --- ViewHolder ---
    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;
        public ViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // --- Helper: Status Icon ---
    private String getStatusIcon(String status) {
        switch (status) {
            case "PENDING": return "\u23F3";   // ⏳
            case "CONFIRMED": return "\u2705"; // ✅
            case "PROCESSING": return "\u2699"; // ⚙
            case "SHIPPED": return "\uD83D\uDE9A"; // 🚚
            case "DELIVERED": return "\uD83D\uDCE6"; // 📦
            case "CANCELLED": return "\u274C"; // ❌
            default: return "";
        }
    }

    // --- Helper: Status Color ---
    private void setStatusColor(ViewHolder holder, String status, android.content.Context context) {
        switch (status) {
            case "PENDING":
                holder.binding.txtStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_pending_bg));
                holder.binding.txtStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.status_pending_text));
                break;
            case "CONFIRMED":
                holder.binding.txtStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_confirmed_bg));
                holder.binding.txtStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.status_confirmed_text));
                break;
            case "PROCESSING":
                holder.binding.txtStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_processing_bg));
                holder.binding.txtStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.status_processing_text));
                break;
            case "SHIPPED":
                holder.binding.txtStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_shipped_bg));
                holder.binding.txtStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.status_shipped_text));
                break;
            case "DELIVERED":
                holder.binding.txtStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_delivered_bg));
                holder.binding.txtStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.status_delivered_text));
                break;
            case "CANCELLED":
                holder.binding.txtStatus.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_cancelled_bg));
                holder.binding.txtStatus.setTextColor(
                        ContextCompat.getColor(context, R.color.status_cancelled_text));
                break;
        }
    }

    // --- Helper: Progress Stepper ---
    private void setupProgressStepper(ViewHolder holder, String status, android.content.Context context) {

        holder.binding.stepPending.setTextColor(ContextCompat.getColor(context, R.color.gray));
        holder.binding.stepConfirmed.setTextColor(ContextCompat.getColor(context, R.color.gray));
        holder.binding.stepProcessing.setTextColor(ContextCompat.getColor(context, R.color.gray));
        holder.binding.stepShipped.setTextColor(ContextCompat.getColor(context, R.color.gray));
        holder.binding.stepDelivered.setTextColor(ContextCompat.getColor(context, R.color.gray));

        switch (status) {
            case "PENDING":
                holder.binding.stepPending.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text));
                break;
            case "CONFIRMED":
                holder.binding.stepPending.setTextColor(ContextCompat.getColor(context, R.color.status_confirmed_text));
                holder.binding.stepConfirmed.setTextColor(ContextCompat.getColor(context, R.color.status_confirmed_text));
                break;
            case "PROCESSING":
                holder.binding.stepPending.setTextColor(ContextCompat.getColor(context, R.color.status_processing_text));
                holder.binding.stepConfirmed.setTextColor(ContextCompat.getColor(context, R.color.status_processing_text));
                holder.binding.stepProcessing.setTextColor(ContextCompat.getColor(context, R.color.status_processing_text));
                break;
            case "SHIPPED":
                holder.binding.stepPending.setTextColor(ContextCompat.getColor(context, R.color.status_shipped_text));
                holder.binding.stepConfirmed.setTextColor(ContextCompat.getColor(context, R.color.status_shipped_text));
                holder.binding.stepProcessing.setTextColor(ContextCompat.getColor(context, R.color.status_shipped_text));
                holder.binding.stepShipped.setTextColor(ContextCompat.getColor(context, R.color.status_shipped_text));
                break;
            case "DELIVERED":
                holder.binding.stepPending.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text));
                holder.binding.stepConfirmed.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text));
                holder.binding.stepProcessing.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text));
                holder.binding.stepShipped.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text));
                holder.binding.stepDelivered.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text));
                break;
            case "CANCELLED":
                // Optional: make all steps gray
                break;
        }
    }
}