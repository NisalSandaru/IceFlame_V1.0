package com.nisal.iceflame.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nisal.iceflame.R;
import com.nisal.iceflame.adapters.CategoryAdapter;
import com.nisal.iceflame.adapters.HomeAdapter;
import com.nisal.iceflame.databinding.FragmentExploreBinding;
import com.nisal.iceflame.model.CategoryDto;
import com.nisal.iceflame.model.ProductDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;

    private CategoryAdapter categoryAdapter;
    private HomeAdapter productAdapter;

    private List<CategoryDto> categoryList = new ArrayList<>();
    private List<ProductDto> productList = new ArrayList<>();

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private GridLayoutManager gridLayoutManager;

    public ExploreFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupAdapters();
        setupSearch();

        loadCategories(); // default
    }

    // ✅ RecyclerView setup
    private void setupRecyclerView() {

        gridLayoutManager = new GridLayoutManager(getContext(), 2); // max 2 columns

        // 🔥 Control span dynamically
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                // If showing categories → full width (1 per row)
                if (binding.recyclerCategories.getAdapter() == categoryAdapter) {
                    return 2;
                }

                // If showing products → 2 items per row
                return 1;
            }
        });

        binding.recyclerCategories.setLayoutManager(gridLayoutManager);
    }

    // ✅ Adapters
    private void setupAdapters() {

        // Category adapter
        categoryAdapter = new CategoryAdapter(categoryList, category -> {

            Bundle bundle = new Bundle();
            bundle.putLong("categoryId", category.getId());

            Fragment fragment = new ListingFragment();
            fragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Product adapter
        productAdapter = new HomeAdapter(getContext(), productList, product -> {

            Bundle bundle = new Bundle();
            bundle.putLong("productId", product.getId());

            ProductDetailsFragment fragment = new ProductDetailsFragment();
            fragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerCategories.setAdapter(categoryAdapter);
    }

    // ✅ Search logic (Debounce 🔥)
    private void setupSearch() {

        binding.textInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String keyword = s.toString().trim();

                    if (keyword.isEmpty()) {
                        showCategories();
                    } else {
                        searchProducts(keyword);
                    }
                };

                handler.postDelayed(searchRunnable, 500); // 🔥 delay
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // ✅ Load Categories
    private void loadCategories() {

        showLoading();

        RetrofitClient.getCategoryApi().getCategories()
                .enqueue(new Callback<List<CategoryDto>>() {
                    @Override
                    public void onResponse(Call<List<CategoryDto>> call,
                                           Response<List<CategoryDto>> response) {

                        hideLoading();

                        if (response.isSuccessful() && response.body() != null) {

                            categoryList.clear();
                            categoryList.addAll(response.body());

                            showCategories();

                        } else {
                            showError("Failed to load categories");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                        hideLoading();
                        showError(t.getMessage());
                    }
                });
    }

    // ✅ Search Products
    private void searchProducts(String keyword) {

        showLoading();

        RetrofitClient.getProductApi()
                .searchProducts(keyword)
                .enqueue(new Callback<List<ProductDto>>() {
                    @Override
                    public void onResponse(Call<List<ProductDto>> call,
                                           Response<List<ProductDto>> response) {

                        hideLoading();

                        if (response.isSuccessful() && response.body() != null) {

                            productList.clear();
                            productList.addAll(response.body());

                            if (productList.isEmpty()) {
                                showEmpty();
                            } else {
                                showProducts();
                            }

                        } else {
                            showError("Search failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                        hideLoading();
                        showError(t.getMessage());
                    }
                });
    }

    // 🎯 UI STATES

    private void showCategories() {
        binding.recyclerCategories.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();
    }

    private void showProducts() {
        binding.recyclerCategories.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showEmpty() {
        binding.recyclerCategories.setAdapter(productAdapter);
        productList.clear();
        productAdapter.notifyDataSetChanged();
        Toasty.info(getContext(), "No results found", Toasty.LENGTH_SHORT).show();
    }

    private void showError(String msg) {
        Toasty.error(getContext(), msg, Toasty.LENGTH_SHORT).show();
    }
}