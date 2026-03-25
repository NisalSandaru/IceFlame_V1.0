package com.nisal.iceflame.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.Gson;
import com.nisal.iceflame.R;
import com.nisal.iceflame.activity.LogInActivity;
import com.nisal.iceflame.databinding.FragmentExploreBinding;
import com.nisal.iceflame.databinding.FragmentSettingBinding;
import com.nisal.iceflame.model.ErrorResponse;
import com.nisal.iceflame.model.User;
import com.nisal.iceflame.network.RetrofitClient;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment {

    private Long userId;
    private User user;
    private FragmentSettingBinding binding;

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
        binding = FragmentSettingBinding.inflate(inflater,container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUser();
        goMyProfile();
        goAddress();
        goMyOrders();
        goLocation();
    }

    private void goAddress(){

        binding.btnAddress.setOnClickListener(v->{
            Fragment fragment = new AddressFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AddressFragment())
                    .addToBackStack("address")
                    .commit();
        });

    }

    private void goMyProfile(){

        binding.btnMyProfile.setOnClickListener(v->{
            Fragment fragment = new ProfileFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack("profile")
                    .commit();
        });

    }

    private void goLocation(){

        binding.btnLocation.setOnClickListener(v->{
            Fragment fragment = new MapFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MapFragment())
                    .addToBackStack("location")
                    .commit();
        });

    }

    private void goMyOrders(){

        binding.btnMyOrders.setOnClickListener(v->{
            Fragment fragment = new MyOrdersFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MyOrdersFragment())
                    .addToBackStack("orders")
                    .commit();
        });

    }

    private void loadUser(){

        RetrofitClient.getUserApi()
                .getUserById(userId)
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        if (!isAdded() || binding == null) return;

                        if (response.isSuccessful() && response.body() != null) {

                            user = response.body();
                            bindUser(user);

                        } else {

                            try {

                                String errorBody = response.errorBody().string();

                                Gson gson = new Gson();
                                ErrorResponse error = gson.fromJson(errorBody, ErrorResponse.class);

                                Toasty.error(
                                        getContext(),
                                        error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            } catch (Exception e) {

                                Toasty.error(
                                        getContext(),
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                        if (!isAdded()) return;

                        Toasty.error(
                                getContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void bindUser(User user){

        Glide.with(this)
                .load(user.getProfileImageUrl())
                .placeholder(R.drawable.account_circle_24px)
                .transform(new MultiTransformation<>(
                        new CenterCrop(),
                        new RoundedCorners(60)
                ))
                .into(binding.userImgProfile);

        binding.tvUserName.setText(user.getFullName());
        binding.tvUserEmail.setText(user.getEmail());
    }
}