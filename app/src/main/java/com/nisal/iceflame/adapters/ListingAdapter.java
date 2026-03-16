package com.nisal.iceflame.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.nisal.iceflame.R;
import com.nisal.iceflame.activity.LogInActivity;
import com.nisal.iceflame.model.AddToCartRequest;
import com.nisal.iceflame.model.CartDto;
import com.nisal.iceflame.model.ProductDto;
import com.nisal.iceflame.model.WishlistDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private List<ProductDto> productList;
    private OnProductClickListener listener;
    private Context context;
    private Set<Long> wishlistIds = new HashSet<>();

    public void setWishlistIds(Set<Long> wishlistIds){
        this.wishlistIds = wishlistIds;
        notifyDataSetChanged();
    }

    // 👇 Interface for product click
    public interface OnProductClickListener {
        void onProductClick(ProductDto product);
    }

    // 👇 Constructor with listener
    public ListingAdapter(Context context, List<ProductDto> productList, OnProductClickListener listener){
        this.context = context;
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

        boolean isWishlisted = wishlistIds.contains(product.getId());

        if(isWishlisted){
            holder.btnWishlist.setImageResource(R.drawable.favorite_fill);
        }else{
            holder.btnWishlist.setImageResource(R.drawable.favorite_24px);
        }

        holder.productTitle.setText(product.getName());
        holder.productPrice.setText("Rs. " + product.getPrice());

        if(product.getRating() != null){
            holder.tvRating.setText(String.valueOf(product.getRating()));
        }

        if(product.getImages() != null && !product.getImages().isEmpty()){
            Glide.with(holder.itemView.getContext())
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.chicken_biryani)
                    .transform(new MultiTransformation<>(
                            new CenterCrop(),
                            new RoundedCorners(15)
                    ))
                    .into(holder.productImage);
        }

        // ⭐ Main card click
        holder.cardProduct.setOnClickListener(v -> {
            if(listener != null){
                listener.onProductClick(product);
            }
        });

        // ❤️ WishlistDto animation
        holder.btnWishlist.setOnClickListener(v -> {

            SharedPreferences prefs =
                    context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

            long userId = prefs.getLong("user_id", -1);

            if(userId == -1){

                Toasty.error(context,"Please login first",Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, LogInActivity.class));
                return;
            }

            long productId = product.getId();

            if(wishlistIds.contains(productId)){

                RetrofitClient.getWishlistApi()
                        .removeWishItem(userId, productId)
                        .enqueue(new Callback<WishlistDto>() {

                            @Override
                            public void onResponse(Call<WishlistDto> call, Response<WishlistDto> response) {

                                wishlistIds.remove(productId);
                                holder.btnWishlist.setImageResource(R.drawable.favorite_24px);

                                Toasty.info(context,"Removed from wishlist",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<WishlistDto> call, Throwable t) {

                                Toasty.error(context,t.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            }else{

                RetrofitClient.getWishlistApi()
                        .addToWishlist(userId, productId)
                        .enqueue(new Callback<WishlistDto>() {

                            @Override
                            public void onResponse(Call<WishlistDto> call, Response<WishlistDto> response) {

                                wishlistIds.add(productId);
                                holder.btnWishlist.setImageResource(R.drawable.favorite_fill);

                                Toasty.success(context,"Added to wishlist ❤️",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<WishlistDto> call, Throwable t) {

                                Toasty.error(context,t.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // 🛒 Cart animation
        holder.btnCart.setOnClickListener(v -> {

            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    ).start();

            addToCartLogic(product);

            Toast.makeText(holder.itemView.getContext(),
                    "Added to cart 🛒",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void addToCartLogic(ProductDto product){

        SharedPreferences prefs =
                context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

        long userId = prefs.getLong("user_id", -1);

        // 🚨 User not logged in
        if(userId == -1){

            Toasty.error(context,
                    "Please login first",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, LogInActivity.class);
            context.startActivity(intent);

            return;
        }

        AddToCartRequest request = AddToCartRequest.builder()
                .productId(product.getId())
                .quantity(1)
                .build();

        RetrofitClient.getCartApi()
                .addToCart(userId, request)
                .enqueue(new Callback<CartDto>() {

                    @Override
                    public void onResponse(Call<CartDto> call, Response<CartDto> response) {

                        if(response.isSuccessful()){

                            Toasty.success(context,
                                    "Added to cart 🛒",
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            Toasty.error(context,
                                    "Failed to add to cart",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartDto> call, Throwable t) {

                        Toasty.error(context,
                                t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
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