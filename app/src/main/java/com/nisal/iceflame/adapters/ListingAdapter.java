package com.nisal.iceflame.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nisal.iceflame.R;
import com.nisal.iceflame.model.ProductDto;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private List<ProductDto> productList;
    private OnProductClickListener listener;

    // 👇 Interface for product click
    public interface OnProductClickListener {
        void onProductClick(ProductDto product);
    }

    // 👇 Constructor with listener
    public ListingAdapter(List<ProductDto> productList, OnProductClickListener listener){
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ProductDto product = productList.get(position);

        holder.productTitle.setText(product.getName());
        holder.productPrice.setText("Rs. " + product.getPrice());

        if(product.getRating() != null){
            holder.tvRating.setText(String.valueOf(product.getRating()));
        }

        if(product.getImages() != null && !product.getImages().isEmpty()){
            Glide.with(holder.itemView.getContext())
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.chicken_biryani)
                    .into(holder.productImage);
        }

        // ⭐ Main card click
        holder.cardProduct.setOnClickListener(v -> {
            if(listener != null){
                listener.onProductClick(product);
            }
        });

        // ❤️ Wishlist animation
        holder.btnWishlist.setOnClickListener(v -> {

            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    ).start();

            Toast.makeText(holder.itemView.getContext(),
                    "Added to wishlist ❤️",
                    Toast.LENGTH_SHORT).show();
        });

        // 🛒 Cart animation
        holder.btnCart.setOnClickListener(v -> {

            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    ).start();

            Toast.makeText(holder.itemView.getContext(),
                    "Added to cart 🛒",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productTitle;
        TextView productPrice;
        TextView tvRating;

        ImageView btnWishlist;
        ImageView btnCart;
        View cardProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.img_product);
            productTitle = itemView.findViewById(R.id.tv_product_title);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            tvRating = itemView.findViewById(R.id.tv_rating);

            btnWishlist = itemView.findViewById(R.id.btn_wishlist);
            btnCart = itemView.findViewById(R.id.btn_cart);
            cardProduct = itemView.findViewById(R.id.card_product);
        }
    }
}