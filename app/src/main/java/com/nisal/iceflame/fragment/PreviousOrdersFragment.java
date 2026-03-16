package com.nisal.iceflame.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisal.iceflame.R;
import com.nisal.iceflame.adapters.OrdersAdapter;
import com.nisal.iceflame.databinding.FragmentPreviousOrdersBinding;
import com.nisal.iceflame.model.OrderDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviousOrdersFragment extends Fragment {

    private FragmentPreviousOrdersBinding binding;
    private List<OrderDto> previousOrders = new ArrayList<>();
    private OrdersAdapter adapter;
    private Long userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPreviousOrdersBinding.inflate(inflater, container, false);

        // Set LayoutManager and Adapter before loading data
        adapter = new OrdersAdapter(previousOrders, order -> {

            Bundle bundle = new Bundle();
            bundle.putLong("orderId", order.getId());

            OrderDetailsFragment fragment = new OrderDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();

        });

        binding.recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerOrders.setAdapter(adapter);

        // Get userId from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        if (userId != -1) {
            loadPreviousOrders();
        }

        return binding.getRoot();
    }

    private void loadPreviousOrders() {
        RetrofitClient.getOrderApi().getPreviousOrders(userId)
                .enqueue(new Callback<List<OrderDto>>() {
                    @Override
                    public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            previousOrders.clear();
                            previousOrders.addAll(response.body());
                            adapter.notifyDataSetChanged();

                            if (previousOrders.isEmpty()) {
                                binding.recyclerOrders.setVisibility(View.GONE);
                                binding.emptyOrdersView.getRoot().setVisibility(View.VISIBLE);
                            } else {
                                binding.recyclerOrders.setVisibility(View.VISIBLE);
                                binding.emptyOrdersView.getRoot().setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                        Toasty.error(requireContext(), "Failed to load previous orders", Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}