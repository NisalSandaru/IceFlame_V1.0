package com.nisal.iceflame.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nisal.iceflame.databinding.FragmentProfileBinding;
import com.nisal.iceflame.model.ErrorResponse;
import com.nisal.iceflame.model.User;
import com.nisal.iceflame.network.RetrofitClient;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Long userId;
    private User user;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("prefs", Context.MODE_PRIVATE);

        userId = prefs.getLong("user_id", -1);

        loadUser();
        setupImagePicker();
        updateProfile();
    }

    // LOAD USER
    private void loadUser(){

        RetrofitClient.getUserApi()
                .getUserById(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        if(response.isSuccessful() && response.body() != null){

                            user = response.body();

                            binding.etFullName.setText(user.getFullName());
                            binding.etEmail.setText(user.getEmail());
                            binding.etMobile.setText(user.getMobileNumber());

                            Glide.with(ProfileFragment.this)
                                    .load(user.getProfileImageUrl())
                                    .placeholder(com.nisal.iceflame.R.drawable.account_circle_24px)
                                    .circleCrop()
                                    .into(binding.imgProfile);

                        }else{

                            Toasty.error(getContext(),"User not found", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                        Toasty.error(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }

    // IMAGE PICKER
    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if(result.getResultCode() == getActivity().RESULT_OK){

                            imageUri = result.getData().getData();

                            Glide.with(this)
                                    .load(imageUri)
                                    .circleCrop()
                                    .into(binding.imgProfile);

                        }

                    }
            );

    private void setupImagePicker(){

        binding.btnChangeImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");

            imagePicker.launch(intent);

        });

    }

    // UPDATE PROFILE
    private void updateProfile(){

        binding.btnSaveProfile.setOnClickListener(v -> {


            if (binding.etFullName.getText() == null){
                Toasty.error(getContext(),"Full name can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.etMobile.getText() == null){
                Toasty.error(getContext(),"Mobile can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setFullName(binding.etFullName.getText().toString());
            user.setMobileNumber(binding.etMobile.getText().toString());

            RetrofitClient.getUserApi()
                    .updateUser(userId, user)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            if(response.isSuccessful()){

                                Toasty.success(getContext(),
                                        "Profile updated",
                                        Toast.LENGTH_SHORT).show();

                            }else{

                                try{

                                    String errorBody = response.errorBody().string();

                                    Gson gson = new Gson();
                                    ErrorResponse error =
                                            gson.fromJson(errorBody, ErrorResponse.class);

                                    Toasty.error(getContext(),
                                            error.getMessage(),
                                            Toast.LENGTH_LONG).show();

                                }catch (Exception e){

                                    Toasty.error(getContext(),
                                            "Update failed",
                                            Toast.LENGTH_SHORT).show();

                                }

                            }

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                            Toasty.error(getContext(),
                                    "Network error",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });

        });

    }

}