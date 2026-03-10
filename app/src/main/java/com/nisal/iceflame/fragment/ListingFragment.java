package com.nisal.iceflame.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nisal.iceflame.R;
import com.nisal.iceflame.adapters.ListingAdapter;
import com.nisal.iceflame.databinding.FragmentListingBinding;
import com.nisal.iceflame.model.ProductDto;
import com.nisal.iceflame.model.WishItemDto;
import com.nisal.iceflame.model.WishlistDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingFragment extends Fragment {

    private FragmentListingBinding binding;
    private ListingAdapter adapter;
    private Long categoryId = 0L;
    private Set<Long> wishlistIds = new HashSet<>();
    private final List<ProductDto> productList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Safe argument handling
        if (getArguments() != null && getArguments().containsKey("categoryId")) {
            categoryId = getArguments().getLong("categoryId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentListingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        showLoading(true);
        loadProducts();
        loadWishlist();

        // ✅ Handle back press properly
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(),
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                requireActivity()
                                        .getSupportFragmentManager()
                                        .popBackStack();
                            }
                        });
    }

    private void loadWishlist() {

        SharedPreferences prefs =
                requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        long userId = prefs.getLong("user_id", -1);

        if(userId == -1) return;

        RetrofitClient.getWishlistApi()
                .getWishlist(userId)
                .enqueue(new Callback<WishlistDto>() {

                    @Override
                    public void onResponse(Call<WishlistDto> call, Response<WishlistDto> response) {

                        if(response.isSuccessful() && response.body()!=null){

                            wishlistIds.clear();

                            if(response.body().getItems()!=null){
                                for(WishItemDto item : response.body().getItems()){
                                    wishlistIds.add(item.getProductId());
                                }
                            }

                            adapter.setWishlistIds(wishlistIds);
                        }
                    }

                    @Override
                    public void onFailure(Call<WishlistDto> call, Throwable t) {

                    }
                });
    }

    // ✅ Setup RecyclerView
    private void setupRecyclerView() {

        binding.recyclerViewListing.setLayoutManager(
                new GridLayoutManager(getContext(), 1)
        );

        adapter = new ListingAdapter(requireContext(), productList, product -> {

            Bundle bundle = new Bundle();
            bundle.putLong("productId", product.getId());

            ProductDetailsFragment detailsFragment = new ProductDetailsFragment();
            detailsFragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerViewListing.setAdapter(adapter);
    }

    // ✅ API Call
    private void loadProducts() {

        RetrofitClient.getProductApi()
                .getProductsByCategory(categoryId)
                .enqueue(new Callback<List<ProductDto>>() {

                    @Override
                    public void onResponse(Call<List<ProductDto>> call,
                                           Response<List<ProductDto>> response) {

                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null) {

                            productList.clear();
                            productList.addAll(response.body());
                            adapter.notifyDataSetChanged();

                        } else {
                            showError("Failed to load products.");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductDto>> call,
                                          Throwable t) {

                        showLoading(false);
                        showError("Error: " + t.getMessage());
                    }
                });
    }

    // ✅ Shimmer Controller
    private void showLoading(boolean isLoading) {

        if (isLoading) {
            binding.shimmerLayout.setVisibility(View.VISIBLE);
            binding.shimmerLayout.startShimmer();
            binding.recyclerViewListing.setVisibility(View.GONE);
        } else {
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.recyclerViewListing.setVisibility(View.VISIBLE);
        }
    }

    // ✅ Error Message
    private void showError(String message) {
        Toasty.error(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    // ✅ Prevent memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}