package com.nisal.iceflame.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nisal.iceflame.R;
import com.nisal.iceflame.adapters.OrderItemsAdapter;
import com.nisal.iceflame.databinding.FragmentOrderDetailsBinding;
import com.nisal.iceflame.model.OrderDto;
import com.nisal.iceflame.network.RetrofitClient;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;

public class OrderDetailsFragment extends Fragment {

    private FragmentOrderDetailsBinding binding;
    private long orderId;

    public OrderDetailsFragment() {
        // Required empty constructor
    }

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

        binding.recyclerItems.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
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

                            binding.txtOrderId.setText("Order #" + order.getId());
                            binding.txtStatus.setText(order.getStatus());
                            binding.txtTotal.setText("Rs. " + order.getTotalAmount());
                            binding.txtDate.setText(order.getCreatedAt());

                            // Items RecyclerView
                            OrderItemsAdapter adapter =
                                    new OrderItemsAdapter(order.getItems(), item -> {

                                        ProductDetailsFragment fragment = new ProductDetailsFragment();

                                        Bundle bundle = new Bundle();
                                        bundle.putLong("productId", item.getProductId());
                                        fragment.setArguments(bundle);

                                        requireActivity()
                                                .getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, fragment)
                                                .addToBackStack(null)
                                                .commit();
                                    });

                            binding.recyclerItems.setAdapter(adapter);

                        }
                    }

                    @Override
                    public void onFailure(Call<OrderDto> call, Throwable t) {

                        if (!isAdded()) return;

                        Toasty.error(requireContext(),
                                "Failed to load order",
                                Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}