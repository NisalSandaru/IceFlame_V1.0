package com.nisal.iceflame.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nisal.iceflame.R;
import com.nisal.iceflame.activity.LogInActivity;
import com.nisal.iceflame.activity.OnboardingActivity;
import com.nisal.iceflame.activity.SplashActivity;
import com.nisal.iceflame.adapters.ProductSliderAdapter;
import com.nisal.iceflame.databinding.FragmentProductDetailsBinding;
import com.nisal.iceflame.model.AddToCartRequest;
import com.nisal.iceflame.model.CartDto;
import com.nisal.iceflame.model.ProductDto;
import com.nisal.iceflame.network.RetrofitClient;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsFragment extends Fragment {

    private FragmentProductDetailsBinding binding;

    private Long productId;
    private int quantity = 1;
    private boolean isWatchlisted = false;

    private ProductDto currentProduct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("productId")) {
            productId = getArguments().getLong("productId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBackPress();
        setupQuantityButtons();
        setupWatchlist();
        setupAddToCart();

        loadProduct();
    }

    // 🔥 Load Product From API
    private void loadProduct() {
        RetrofitClient.getProductApi()
                .getProductById(productId)
                .enqueue(new Callback<ProductDto>() {
                    @Override
                    public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                        if (!isAdded() || binding == null) return;

                        if (response.isSuccessful() && response.body() != null) {
                            currentProduct = response.body();
                            bindProduct(currentProduct);
                        } else {
                            showError("Product not found");
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductDto> call, Throwable t) {
                        if (!isAdded()) return;
                        showError(t.getMessage());
                    }
                });
    }

    // 🔥 Bind Product Data
    private void bindProduct(ProductDto product) {

        binding.tvProductName.setText(product.getName());
        binding.tvProductDescription.setText(product.getDescription());
        binding.tvPrice.setText("Rs." + product.getPrice());

        binding.ratingBar.setRating(
                product.getRating() != null
                        ? product.getRating().floatValue()
                        : 0f
        );

        if (product.getKcal() != null) {
            binding.tvKcal.setText(product.getKcal() + " kcal");
        } else {
            binding.tvKcal.setText("000 kcal");
        }

        // TODO: Setup ViewPager2 adapter for images here
        ProductSliderAdapter adapter = new ProductSliderAdapter(product.getImages());
        binding.productImageSlider.setAdapter(adapter);

        // Attach dots indicator to ViewPager2
        binding.dotsIndicator.setViewPager2(binding.productImageSlider);
    }

    // 🔥 Quantity Logic
    private void setupQuantityButtons() {

        binding.tvQuantity.setText(String.valueOf(quantity));

        binding.btnPlus.setOnClickListener(v -> {
            quantity++;
            binding.tvQuantity.setText(String.valueOf(quantity));
        });

        binding.btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantity.setText(String.valueOf(quantity));
            }
        });
    }

    // 🔥 Watchlist Toggle
    private void setupWatchlist() {
        binding.btnWatchlist.setOnClickListener(v -> {
            isWatchlisted = !isWatchlisted;

            if (isWatchlisted) {
                binding.btnWatchlist.setImageResource(R.drawable.favorite_fill);
            } else {
                binding.btnWatchlist.setImageResource(R.drawable.favorite_24px);
            }
        });
    }

    // 🔥 Add To Cart
    private void setupAddToCart() {

        binding.btnAddToCart.setOnClickListener(v -> {

            if (currentProduct == null) return;

            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences("prefs", getContext().MODE_PRIVATE);

            Long userId = prefs.getLong("user_id", -1);

            // 🚨 User not logged in
            if (userId == -1) {

                Toasty.error(getContext(),
                        "Please login first",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), LogInActivity.class);
                startActivity(intent);

                return; // ❗ stop execution
            }

            AddToCartRequest request = AddToCartRequest.builder()
                    .productId(currentProduct.getId())
                    .quantity(quantity)
                    .build();

            RetrofitClient.getCartApi()
                    .addToCart(userId, request)
                    .enqueue(new Callback<CartDto>() {

                        @Override
                        public void onResponse(Call<CartDto> call, Response<CartDto> response) {

                            if (!isAdded()) return;

                            if (response.isSuccessful()) {

                                Toasty.success(
                                        getContext(),
                                        "Added to cart",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toasty.error(
                                        getContext(),
                                        "Failed to add to cart",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CartDto> call, Throwable t) {

                            if (!isAdded()) return;

                            Toasty.error(
                                    getContext(),
                                    t.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

        });
    }

    // 🔥 Back Press Handling
    private void setupBackPress() {

        requireActivity()
                .getOnBackPressedDispatcher()
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

    // 🔥 Utility
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}