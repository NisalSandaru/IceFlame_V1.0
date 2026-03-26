package com.nisal.iceflame.fragment;

import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nisal.iceflame.adapters.OrderItemsAdapter;
import com.nisal.iceflame.databinding.FragmentOrderDetailsBinding;
import com.nisal.iceflame.model.OrderDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;

public class OrderDetailsFragment extends Fragment {

    private FragmentOrderDetailsBinding binding;
    private long orderId;

    public OrderDetailsFragment() { }

    public static OrderDetailsFragment newInstance(long orderId) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("orderId", orderId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getLong("orderId");
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecycler();
        loadOrderDetails();
    }

    private void setupRecycler() {
        binding.recyclerItems.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadOrderDetails() {
        RetrofitClient.getOrderApi()
                .getById(orderId)
                .enqueue(new retrofit2.Callback<OrderDto>() {

                    @Override
                    public void onResponse(Call<OrderDto> call, retrofit2.Response<OrderDto> response) {

                        if (!isAdded() || binding == null) return;

                        if (response.isSuccessful() && response.body() != null) {

                            OrderDto order = response.body();

                            // Set basic info
                            binding.txtOrderId.setText("Order #" + order.getId());
                            binding.txtStatus.setText(order.getStatus());
                            binding.txtTotal.setText("Rs. " + order.getTotalAmount());

                            // Date
                            try {
                                LocalDateTime ldt = LocalDateTime.parse(order.getCreatedAt());
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy • h:mm a");
                                binding.txtDate.setText(ldt.format(formatter));
                            } catch (Exception e) {
                                binding.txtDate.setText(order.getCreatedAt());
                            }

                            // Animate progress, status, and colors
                            setupStatusUI(order);

                            // Recycler for items
                            OrderItemsAdapter adapter =
                                    new OrderItemsAdapter(order.getItems(), item -> {
                                        ProductDetailsFragment fragment = new ProductDetailsFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putLong("productId", item.getProductId());
                                        fragment.setArguments(bundle);
                                        requireActivity()
                                                .getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(com.nisal.iceflame.R.id.fragment_container, fragment)
                                                .addToBackStack(null)
                                                .commit();
                                    });
                            binding.recyclerItems.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderDto> call, Throwable t) {
                        if (!isAdded()) return;
                        Toasty.error(requireContext(), "Failed to load order", Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupStatusUI(OrderDto order) {
        String status = order.getStatus().toUpperCase();

        int progress = 0;
        String label = "";
        String colorHex = "#FF5722"; // default orange

        switch (status) {
            case "PENDING":
                progress = 10; label = "Waiting for confirmation ⏳"; colorHex = "#FFC107"; break;
            case "CONFIRMED":
                progress = 30; label = "Order confirmed ✅"; colorHex = "#03A9F4"; break;
            case "PROCESSING":
                progress = 60; label = "Preparing your food 🍳"; colorHex = "#FF5722"; break;
            case "SHIPPED":
                progress = 85; label = "On the way 🚚"; colorHex = "#9C27B0"; break;
            case "DELIVERED":
                progress = 100; label = "Delivered 📦"; colorHex = "#4CAF50"; break;
            case "CANCELLED":
                progress = 0; label = "Order cancelled ❌"; colorHex = "#F44336"; break;
            default:
                progress = 0; label = ""; colorHex = "#AAAAAA"; break;
        }

        // Animate ProgressBar
        ObjectAnimator animator = ObjectAnimator.ofInt(binding.orderProgress, "progress", 0, progress);
        animator.setDuration(800);
        animator.start();

        // Set progress color
        binding.orderProgress.setProgressTintList(ColorStateList.valueOf(Color.parseColor(colorHex)));

        // Set badge color
        binding.txtStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorHex)));

        // Set progress label
        binding.txtProgressLabel.setText(label);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}