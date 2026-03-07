package com.nisal.iceflame.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.nisal.iceflame.R;
import com.nisal.iceflame.model.CartItemDto;
import com.bumptech.glide.Glide;
import com.nisal.iceflame.model.ProductDto;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItemDto> cartItems;
    private CartListener listener;
    private OnProductClickListener productListener;

    public interface OnProductClickListener {
        void onProductClick(Long productId);
    }

    public interface CartListener {
        void onQuantityChanged(CartItemDto item);
        void onRemoveItem(CartItemDto item);
    }

    public CartAdapter(List<CartItemDto> cartItems, CartListener listener, OnProductClickListener productListener) {
        this.productListener = productListener;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemDto item = cartItems.get(position);

        holder.title.setText(item.getProductName());
        holder.price.setText("Rs." + item.getPrice());
        holder.quantity.setText(String.valueOf(item.getQuantity()));
        holder.rating.setText(String.valueOf(item.getRating())); // Placeholder rating

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.chicken_biryani)
                .transform(new MultiTransformation<>(
                        new CenterCrop(),
                        new RoundedCorners(15)
                ))
                .into(holder.image);

        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            holder.quantity.setText(String.valueOf(item.getQuantity()));
            listener.onQuantityChanged(item);
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                holder.quantity.setText(String.valueOf(item.getQuantity()));
                listener.onQuantityChanged(item);
            }
        });

        holder.card_cart.setOnClickListener(v -> {
            if(productListener != null){
                productListener.onProductClick(item.getProductId());
            }
        });

        holder.btnRemove.setOnClickListener(v -> listener.onRemoveItem(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price, quantity, rating;
        ImageButton btnPlus, btnMinus, btnRemove;
        View card_cart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cart_img_product);
            title = itemView.findViewById(R.id.cart_tv_product_title);
            price = itemView.findViewById(R.id.cart_tv_product_price);
            quantity = itemView.findViewById(R.id.cart_tv_quantity);
            rating = itemView.findViewById(R.id.cart_tv_rating);
            btnPlus = itemView.findViewById(R.id.cart_btn_plus);
            btnMinus = itemView.findViewById(R.id.cart_btn_minus);
            btnRemove = itemView.findViewById(R.id.cart_btn_remove);
            card_cart = itemView.findViewById(R.id.card_cart);
        }
    }
}