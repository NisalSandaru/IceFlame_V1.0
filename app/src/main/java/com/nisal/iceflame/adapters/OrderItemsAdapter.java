package com.nisal.iceflame.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.nisal.iceflame.R;
import com.nisal.iceflame.databinding.ItemOrderProductBinding;
import com.nisal.iceflame.model.OrderItemDto;
import com.nisal.iceflame.model.ProductDto;

import java.util.List;
import java.util.Locale;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>{

    private List<OrderItemDto> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OrderItemDto item);
    }

    public OrderItemsAdapter(List<OrderItemDto> items, OnItemClickListener listener){
        this.items = items;
        this.listener = listener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemOrderProductBinding binding =
                ItemOrderProductBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderItemDto item = items.get(position);

        // ---------- Glide Image ----------
        String imageUrl = item.getImgUrl();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl == null || imageUrl.isEmpty()
                        ? R.drawable.chicken_biryani
                        : imageUrl)
                .placeholder(R.drawable.chicken_biryani)
                .error(R.drawable.chicken_biryani)
                .transform(new MultiTransformation<>(
                        new CenterCrop(),
                        new RoundedCorners(15)
                ))
                .into(holder.binding.imgProduct);

        // ---------- Product Name ----------
        holder.binding.txtProductName.setText(item.getProductName());

        // ---------- Quantity ----------
        holder.binding.txtQty.setText("Qty: " + item.getQuantity());
        holder.binding.txtQtyBadge.setText("x" +item.getQuantity());

        // ---------- Price Formatting ----------
        String formattedPrice = String.format(Locale.getDefault(),"Rs. %.2f", item.getPrice());
        holder.binding.txtPrice.setText(formattedPrice);

        holder.binding.oderItemCard.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ItemOrderProductBinding binding;

        public ViewHolder(ItemOrderProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}