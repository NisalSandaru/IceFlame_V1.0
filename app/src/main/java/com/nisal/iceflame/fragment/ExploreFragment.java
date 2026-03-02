package com.nisal.iceflame.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nisal.iceflame.R;
import com.nisal.iceflame.adapters.CategoryAdapter;
import com.nisal.iceflame.databinding.FragmentExploreBinding;
import com.nisal.iceflame.model.CategoryDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private CategoryAdapter adapter;
    private List<CategoryDto> categoryList = new ArrayList<>();

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater,container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerCategories.setLayoutManager(new GridLayoutManager(getContext(), 1));

        adapter = new CategoryAdapter(categoryList, category -> {
            Toast.makeText(getContext(), "Clicked: " + category.getName(), Toast.LENGTH_SHORT).show();
            // TODO: open products fragment for this category
        });

        binding.recyclerCategories.setAdapter(adapter);

        loadCategoriesFromBackend();
    }

    private void loadCategoriesFromBackend() {
        RetrofitClient.getCategoryApi().getCategories()
                .enqueue(new Callback<List<CategoryDto>>() {
                    @Override
                    public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoryList.clear();
                            categoryList.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}