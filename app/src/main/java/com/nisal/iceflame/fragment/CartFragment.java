package com.nisal.iceflame.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisal.iceflame.R;
import com.nisal.iceflame.activity.LogInActivity;
import com.nisal.iceflame.adapters.CartAdapter;
import com.nisal.iceflame.databinding.FragmentCartBinding;
import com.nisal.iceflame.model.CartDto;
import com.nisal.iceflame.model.CartItemDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private List<CartItemDto> cartItems = new ArrayList<>();
    private CartAdapter adapter;
    private Long userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get logged-in user ID
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
        binding = FragmentCartBinding.inflate(inflater, container, false);

        // Set LayoutManager before adapter
        binding.cartRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup Adapter
        adapter = new CartAdapter(cartItems, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged(CartItemDto item) {
                updateCartItemInBackend(item);
                updateTotal();
            }

            @Override
            public void onRemoveItem(CartItemDto item) {
                removeCartItemFromBackend(item);
                cartItems.remove(item);
                adapter.notifyDataSetChanged();
                updateTotal();
                checkCartItems();
            }
        }, productId -> {
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
        });

        binding.cartRecycler.setAdapter(adapter);

        // Load cart from backend
        loadCart();

        // Handle back press
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        return binding.getRoot();
    }

    // Load cart from backend
    private void loadCart() {
        RetrofitClient.getCartApi()
                .getCart(userId)
                .enqueue(new Callback<CartDto>() {
                    @Override
                    public void onResponse(Call<CartDto> call, Response<CartDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CartDto cart = response.body();
                            cartItems.clear();
                            if (cart.getItems() != null) {
                                cartItems.addAll(cart.getItems());
                            }
                            adapter.notifyDataSetChanged();
                            updateTotal();
                            checkCartItems();
                        } else {
                            Toasty.warning(requireContext(), "Cart is empty", Toasty.LENGTH_SHORT).show();
                            cartItems.clear();
                            adapter.notifyDataSetChanged();
                            checkCartItems();
                        }
                    }

                    @Override
                    public void onFailure(Call<CartDto> call, Throwable t) {
                        Toasty.error(requireContext(), "Error: " + t.getMessage(), Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    // Update total price
    private void updateTotal() {
        double subtotal = 0;
        for (CartItemDto item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        double deliveryPrice = 400;

        binding.tvSubtotal.setText("Rs." + subtotal);
        binding.tvDelivery.setText("Rs." + deliveryPrice);
        binding.tvTotal.setText("Rs."+ (subtotal+deliveryPrice));
    }

    // Show empty cart view if needed
    private void checkCartItems() {
        if (cartItems.isEmpty()) {
            binding.cartRecycler.setVisibility(View.GONE);
            binding.emptyCartView.getRoot().setVisibility(View.VISIBLE);
            binding.summaryLayout.setVisibility(View.GONE);
            binding.bottomBar.setVisibility(View.GONE);
        } else {
            binding.cartRecycler.setVisibility(View.VISIBLE);
            binding.emptyCartView.getRoot().setVisibility(View.GONE);
            binding.summaryLayout.setVisibility(View.VISIBLE);
            binding.bottomBar.setVisibility(View.VISIBLE);
        }
    }

    // Update quantity in backend
    private void updateCartItemInBackend(CartItemDto item) {
        RetrofitClient.getCartApi()
                .updateQuantity(item.getId(), item.getQuantity())
                .enqueue(new Callback<CartDto>() {
                    @Override
                    public void onResponse(Call<CartDto> call, Response<CartDto> response) {
                        // Optional: handle response
                    }

                    @Override
                    public void onFailure(Call<CartDto> call, Throwable t) {
                        Toasty.error(requireContext(), "Failed to update quantity", Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    // Remove item from backend
    private void removeCartItemFromBackend(CartItemDto item) {
        RetrofitClient.getCartApi()
                .removeItem(item.getId())
                .enqueue(new Callback<CartDto>() {
                    @Override
                    public void onResponse(Call<CartDto> call, Response<CartDto> response) {
                        // Optional: handle response
                    }

                    @Override
                    public void onFailure(Call<CartDto> call, Throwable t) {
                        Toasty.error(requireContext(), "Failed to remove item", Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}