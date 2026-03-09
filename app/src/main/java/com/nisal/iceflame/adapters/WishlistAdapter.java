package com.nisal.iceflame.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.nisal.iceflame.R;
import com.nisal.iceflame.model.WishItemDto;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private List<WishItemDto> items;
    private OnRemoveClickListener listener;
    private OnProductClickListener productListener;

    public WishlistAdapter(List<WishItemDto> items,
                           OnRemoveClickListener listener,
                           OnProductClickListener productListener) {
        this.items = items;
        this.listener = listener;
        this.productListener = productListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wishlist, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WishItemDto item = items.get(position);

        holder.title.setText(item.getProductName());
        holder.price.setText("Rs. " + item.getPrice());
        holder.rating.setText(String.valueOf(item.getRating()));

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.chicken_biryani)
                .transform(new MultiTransformation<>(
                        new CenterCrop(),
                        new RoundedCorners(15)
                ))
                .into(holder.image);

        // Product click
        holder.cardWish.setOnClickListener(v -> {
            if (productListener != null) {
                productListener.onProductClick(item.getProductId());
            }
        });

        // Remove click
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        ImageButton btnRemove;
        TextView title, price, rating;
        View cardWish;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.wish_img_product);
            title = itemView.findViewById(R.id.wish_tv_product_title);
            price = itemView.findViewById(R.id.wish_tv_product_price);
            rating = itemView.findViewById(R.id.wish_tv_rating);
            cardWish = itemView.findViewById(R.id.card_wish);
            btnRemove = itemView.findViewById(R.id.wish_btn_remove);
        }
    }

    // Remove listener
    public interface OnRemoveClickListener {
        void onRemoveItem(WishItemDto item);
    }

    // Product click listener
    public interface OnProductClickListener {
        void onProductClick(Long productId);
    }
}