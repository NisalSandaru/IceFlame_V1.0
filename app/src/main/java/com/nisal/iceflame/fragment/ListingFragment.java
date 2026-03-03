package com.nisal.iceflame.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nisal.iceflame.adapters.ListingAdapter;
import com.nisal.iceflame.databinding.FragmentListingBinding;
import com.nisal.iceflame.model.ProductDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingFragment extends Fragment {

    private FragmentListingBinding binding;
    private ListingAdapter adapter;
    private Long categoryId;
    private List<ProductDto> productList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            categoryId = getArguments().getLong("categoryId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewListing.setLayoutManager(new GridLayoutManager(getContext(),1));

        adapter = new ListingAdapter(productList);
        binding.recyclerViewListing.setAdapter(adapter);

        loadProducts();
    }

    private void loadProducts(){
        RetrofitClient.getProductApi()
                .getProductsByCategory(categoryId)
                .enqueue(new Callback<List<ProductDto>>() {
                    @Override
                    public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                        if(response.isSuccessful() && response.body() != null){
                            productList.clear();
                            productList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(),"Failed to load products",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                        Toast.makeText(getContext(),"Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}