package com.nisal.iceflame.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.nisal.iceflame.activity.LogInActivity;
import com.nisal.iceflame.databinding.ItemHomeProductBinding;
import com.nisal.iceflame.model.AddToCartRequest;
import com.nisal.iceflame.model.CartDto;
import com.nisal.iceflame.model.ProductDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private List<ProductDto> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(ProductDto product);
    }

    private Context context;

    public HomeAdapter(Context context, List<ProductDto> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemHomeProductBinding binding;

        public ViewHolder(ItemHomeProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemHomeProductBinding binding = ItemHomeProductBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ProductDto product = list.get(position);

        holder.binding.tvProductTitle.setText(product.getName());
        holder.binding.tvProductPrice.setText("Rs. " + product.getPrice());

        if(product.getRating() != null){
            holder.binding.tvRating.setText("⭐ " + product.getRating());
        }

        if(product.getImages() != null && !product.getImages().isEmpty()){
            Glide.with(holder.itemView.getContext())
                    .load(product.getImages().get(0))
                    .transform(new MultiTransformation<>(
                            new CenterCrop(),
                            new RoundedCorners(20)
                    ))
                    .into(holder.binding.imgProduct);
        }

        // Click → open details
        holder.binding.getRoot().setOnClickListener(v -> {
            if(listener != null){
                listener.onClick(product);
            }
        });

        // Cart click
        holder.binding.btnCart.setOnClickListener(v -> {

            // 🔥 Animation
            v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    ).start();

            addToCart(product);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void addToCart(ProductDto product){

        SharedPreferences prefs =
                context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

        long userId = prefs.getLong("user_id", -1);

        // ❌ Not logged in
        if(userId == -1){

            Toasty.error(context,
                    "Please login first",
                    Toast.LENGTH_SHORT).show();

            context.startActivity(new Intent(context, LogInActivity.class));
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
}