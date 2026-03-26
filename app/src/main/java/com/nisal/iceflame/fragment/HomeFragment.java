package com.nisal.iceflame.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.nisal.iceflame.R;
import com.nisal.iceflame.adapters.BannerAdapter;
import com.nisal.iceflame.adapters.HomeAdapter;
import com.nisal.iceflame.databinding.FragmentHomeBinding;
import com.nisal.iceflame.model.CategoryDto;
import com.nisal.iceflame.model.ProductDto;
import com.nisal.iceflame.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeAdapter adapter;

    private final List<ProductDto> homeProducts = new ArrayList<>();
    private final List<CategoryDto> categoryList = new ArrayList<>();

    private Handler bannerHandler;
    private Runnable bannerRunnable;

    // Pagination
    private int currentPage = 0;
    private final int pageSize = 6;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private final List<ProductDto> fullList = new ArrayList<>();

    // 🔥 Shake Detection
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener shakeListener;
    private static final float SHAKE_THRESHOLD = 12f;
    private long lastShakeTime = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecycler();
        setupBanner();
        setupShakeDetector();

        loadCategories();
    }

    // =========================
    // RecyclerView
    // =========================
    private void setupRecycler() {

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerView.setLayoutManager(layoutManager);

        adapter = new HomeAdapter(requireContext(), homeProducts, product -> {

            Bundle bundle = new Bundle();
            bundle.putLong("productId", product.getId());

            ProductDetailsFragment fragment = new ProductDetailsFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy <= 0) return;

                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && !isLastPage && lastVisible >= totalItemCount - 2) {
                    loadNextPage();
                }
            }
        });
    }

    // =========================
    // Categories
    // =========================
    private void loadCategories() {

        RetrofitClient.getCategoryApi()
                .getCategories()
                .enqueue(new Callback<List<CategoryDto>>() {

                    @Override
                    public void onResponse(Call<List<CategoryDto>> call,
                                           Response<List<CategoryDto>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            categoryList.clear();
                            categoryList.addAll(response.body());

                            setupTabs();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CategoryDto>> call, Throwable t) {}
                });
    }

    // =========================
    // Tabs
    // =========================
    private void setupTabs() {

        binding.tabLayout.removeAllTabs();

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"));

        for (CategoryDto category : categoryList) {
            binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(category.getName())
            );
        }

        binding.tabLayout.clearOnTabSelectedListeners();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();

                homeProducts.clear();
                adapter.notifyDataSetChanged();

                currentPage = 0;
                isLastPage = false;

                binding.recyclerView.scrollToPosition(0);

                if (position == 0) {
                    loadAllProducts();
                } else {
                    CategoryDto selected = categoryList.get(position - 1);
                    loadProductsByCategory(selected.getId());
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadAllProducts();

        TabLayout.Tab allTab = binding.tabLayout.getTabAt(0);
        if (allTab != null) allTab.select();
    }

    // =========================
    // Load Products
    // =========================
    private void loadAllProducts() {

        RetrofitClient.getProductApi()
                .getAllProducts()
                .enqueue(new Callback<List<ProductDto>>() {

                    @Override
                    public void onResponse(Call<List<ProductDto>> call,
                                           Response<List<ProductDto>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            fullList.clear();
                            fullList.addAll(response.body());

                            currentPage = 0;
                            isLastPage = false;

                            homeProducts.clear();
                            loadNextPage();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductDto>> call, Throwable t) {}
                });
    }

    private void loadProductsByCategory(Long categoryId) {

        RetrofitClient.getProductApi()
                .getProductsByCategory(categoryId)
                .enqueue(new Callback<List<ProductDto>>() {

                    @Override
                    public void onResponse(Call<List<ProductDto>> call,
                                           Response<List<ProductDto>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            fullList.clear();
                            fullList.addAll(response.body());

                            currentPage = 0;
                            isLastPage = false;

                            homeProducts.clear();
                            loadNextPage();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductDto>> call, Throwable t) {}
                });
    }

    // =========================
    // Pagination
    // =========================
    private void loadNextPage() {

        if (isLoading || isLastPage) return;

        isLoading = true;

        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, fullList.size());

        if (start >= fullList.size()) {
            isLastPage = true;
            isLoading = false;
            return;
        }

        List<ProductDto> subList = fullList.subList(start, end);

        homeProducts.addAll(subList);
        adapter.notifyDataSetChanged();

        currentPage++;

        if (end >= fullList.size()) {
            isLastPage = true;
        }

        isLoading = false;
    }

    // =========================
    // Shake Detector
    // =========================
    private void setupShakeDetector() {

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        shakeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

                if (acceleration > SHAKE_THRESHOLD) {

                    long currentTime = System.currentTimeMillis();

                    if (currentTime - lastShakeTime > 1500) {
                        lastShakeTime = currentTime;

                        Toast.makeText(getContext(), "Reloading...", Toast.LENGTH_SHORT).show();
                        reloadData();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    }

    private void reloadData() {

        currentPage = 0;
        isLastPage = false;

        homeProducts.clear();
        adapter.notifyDataSetChanged();

        int selectedTab = binding.tabLayout.getSelectedTabPosition();

        if (selectedTab == 0) {
            loadAllProducts();
        } else {
            CategoryDto selected = categoryList.get(selectedTab - 1);
            loadProductsByCategory(selected.getId());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(shakeListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(shakeListener);
        }
    }

    // =========================
    // Banner
    // =========================
    private void setupBanner() {

        List<Integer> banners = new ArrayList<>();
        banners.add(R.drawable.banner1);
        banners.add(R.drawable.banner2);
        banners.add(R.drawable.banner3);

        BannerAdapter adapter = new BannerAdapter(banners);
        binding.bannerViewPager.setAdapter(adapter);

        bannerHandler = new Handler(Looper.getMainLooper());

        bannerRunnable = () -> {
            int next = (binding.bannerViewPager.getCurrentItem() + 1) % banners.size();
            binding.bannerViewPager.setCurrentItem(next, true);
            bannerHandler.postDelayed(bannerRunnable, 3000);
        };

        bannerHandler.postDelayed(bannerRunnable, 3000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }

        binding = null;
    }
}