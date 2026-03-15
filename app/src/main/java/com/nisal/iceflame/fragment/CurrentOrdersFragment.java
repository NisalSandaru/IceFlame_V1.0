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

import com.nisal.iceflame.adapters.OrdersAdapter;
import com.nisal.iceflame.databinding.FragmentCurrentOrdersBinding;
import com.nisal.iceflame.model.OrderDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentOrdersFragment extends Fragment {

    private FragmentCurrentOrdersBinding binding;
    private List<OrderDto> currentOrders = new ArrayList<>();
    private OrdersAdapter adapter;
    private Long userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCurrentOrdersBinding.inflate(inflater, container, false);

        // Setup RecyclerView
        adapter = new OrdersAdapter(currentOrders);
        binding.recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerOrders.setAdapter(adapter);

        // Get logged user id
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("prefs", Context.MODE_PRIVATE);

        userId = prefs.getLong("user_id", -1);

        if (userId != -1) {
            loadCurrentOrders();
        }

        return binding.getRoot();
    }

    private void loadCurrentOrders() {

        RetrofitClient.getOrderApi()
                .getCurrentOrders(userId)
                .enqueue(new Callback<List<OrderDto>>() {

                    @Override
                    public void onResponse(Call<List<OrderDto>> call,
                                           Response<List<OrderDto>> response) {

                        if (!isAdded() || binding == null) return;

                        if (response.isSuccessful() && response.body() != null) {

                            currentOrders.clear();
                            currentOrders.addAll(response.body());

                            adapter.notifyDataSetChanged();

                            if (currentOrders.isEmpty()) {

                                binding.recyclerOrders.setVisibility(View.GONE);
                                binding.emptyOrdersView.getRoot().setVisibility(View.VISIBLE);

                            } else {

                                binding.recyclerOrders.setVisibility(View.VISIBLE);
                                binding.emptyOrdersView.getRoot().setVisibility(View.GONE);

                            }

                        } else {

                            Toasty.warning(requireContext(),
                                    "No current orders found",
                                    Toasty.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<List<OrderDto>> call, Throwable t) {

                        if (!isAdded()) return;

                        Toasty.error(requireContext(),
                                "Failed to load orders",
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