package com.nisal.iceflame.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisal.iceflame.R;
import com.nisal.iceflame.activity.LogInActivity;
import com.nisal.iceflame.adapters.WishlistAdapter;
import com.nisal.iceflame.databinding.FragmentWishlistBinding;
import com.nisal.iceflame.model.WishItemDto;
import com.nisal.iceflame.model.WishlistDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistFragment extends Fragment {

    private FragmentWishlistBinding binding;
    private List<WishItemDto> wishItems = new ArrayList<>();
    private WishlistAdapter adapter;
    private Long userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("prefs", Context.MODE_PRIVATE);

        userId = prefs.getLong("user_id", -1);

        if (userId == -1) {
            Toasty.error(requireContext(), "Please login first", Toasty.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LogInActivity.class));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWishlistBinding.inflate(inflater, container, false);

        setupRecycler();

        loadWishlist();

        return binding.getRoot();
    }

    private void setupRecycler() {

        adapter = new WishlistAdapter(
                wishItems,
                item -> removeFromWishlist(item),
                productId -> {
                    // TODO: open product details page
                    Bundle bundle = new Bundle();
                    bundle.putLong("productId", productId);

                    ProductDetailsFragment detailsFragment = new ProductDetailsFragment();
                    detailsFragment.setArguments(bundle);

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, detailsFragment)
                            .addToBackStack(null)
                            .commit();
                }
        );

        binding.wishlistRecycler.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        binding.wishlistRecycler.setAdapter(adapter);
    }

    private void loadWishlist() {

        RetrofitClient.getWishlistApi()
                .getWishlist(userId)
                .enqueue(new Callback<WishlistDto>() {

                    @Override
                    public void onResponse(Call<WishlistDto> call, Response<WishlistDto> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            WishlistDto wishlist = response.body();

                            wishItems.clear();

                            if (wishlist.getItems() != null) {
                                wishItems.addAll(wishlist.getItems());
                            }

                            updateUI();

                        } else {

                            wishItems.clear();
                            updateUI();

                            Toasty.info(requireContext(),
                                    "Wishlist is empty",
                                    Toasty.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WishlistDto> call, Throwable t) {

                        Toasty.error(requireContext(),
                                "Error: " + t.getMessage(),
                                Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFromWishlist(WishItemDto item) {

        RetrofitClient.getWishlistApi()
                .removeWishItem(userId, item.getProductId())
                .enqueue(new Callback<WishlistDto>() {

                    @Override
                    public void onResponse(Call<WishlistDto> call, Response<WishlistDto> response) {

                        if (response.isSuccessful()) {

                            wishItems.remove(item);
                            updateUI();

                            Toasty.success(requireContext(),
                                    "Removed from wishlist",
                                    Toasty.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WishlistDto> call, Throwable t) {

                        Toasty.error(requireContext(),
                                "Failed: " + t.getMessage(),
                                Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI() {

        if (wishItems.isEmpty()) {

            binding.wishlistRecycler.setVisibility(View.GONE);
            binding.emptyWishlistView.getRoot().setVisibility(View.VISIBLE);

        } else {

            binding.wishlistRecycler.setVisibility(View.VISIBLE);
            binding.emptyWishlistView.getRoot().setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }
}