package com.nisal.iceflame.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.nisal.iceflame.databinding.FragmentProfileBinding;
import com.nisal.iceflame.model.User;
import com.nisal.iceflame.network.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
        setupSaveButton();
    }

    // ================= LOAD USER =================
    private void loadUser() {

        RetrofitClient.getUserApi()
                .getUserById(userId)
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            user = response.body();

                            binding.etFullName.setText(user.getFullName());
                            binding.etEmail.setText(user.getEmail());
                            binding.etMobile.setText(user.getMobileNumber());

                            Glide.with(ProfileFragment.this)
                                    .load(user.getProfileImageUrl())
                                    .circleCrop()
                                    .into(binding.imgProfile);

                        } else {
                            Toasty.error(getContext(), "User not found", Toasty.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toasty.error(getContext(), t.getMessage(), Toasty.LENGTH_SHORT).show();
                    }
                });
    }

    // ================= CAMERA =================
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(),
                    result -> {
                        if (result) {
                            previewImage(imageUri);
                        }
                    });

    private void openCamera() {
        imageUri = createImageUri();
        cameraLauncher.launch(imageUri);
    }

    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        return requireActivity().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    // ================= GALLERY =================
    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == Activity.RESULT_OK &&
                                result.getData() != null) {

                            imageUri = result.getData().getData();

                            if (imageUri != null) {
                                previewImage(imageUri);
                            }
                        }
                    });

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePicker.launch(intent);
    }

    // ================= IMAGE PICK =================
    private void setupImagePicker() {

        binding.btnChangeImage.setOnClickListener(v -> {

            String[] options = {"Camera", "Gallery"};

            new AlertDialog.Builder(getContext())
                    .setTitle("Select Image")
                    .setItems(options, (dialog, which) -> {

                        if (which == 0) {

                            if (ContextCompat.checkSelfPermission(requireContext(),
                                    Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED) {
                                openCamera();
                            } else {
                                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                            }

                        } else {

                            String permission = android.os.Build.VERSION.SDK_INT >= 33
                                    ? Manifest.permission.READ_MEDIA_IMAGES
                                    : Manifest.permission.READ_EXTERNAL_STORAGE;

                            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                                    == PackageManager.PERMISSION_GRANTED) {
                                openGallery();
                            } else {
                                requestPermissionLauncher.launch(permission);
                            }
                        }

                    }).show();
        });
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (!isGranted) {
                            Toasty.error(getContext(), "Permission denied", Toasty.LENGTH_SHORT).show();
                        }
                    });

    // ================= PREVIEW =================
    private void previewImage(Uri uri) {

        Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.imgProfile);
    }

    // ================= SAVE BUTTON =================
    private void setupSaveButton() {

        binding.btnSaveProfile.setOnClickListener(v -> {

            binding.progressBar.setVisibility(View.VISIBLE);

            if (imageUri != null) {
                uploadToCloudinary(imageUri);
            } else {
                updateUser();
            }
        });
    }

    // ================= CLOUDINARY =================
    private void uploadToCloudinary(Uri uri) {

        try {

            InputStream inputStream = requireContext()
                    .getContentResolver().openInputStream(uri);

            byte[] bytes = readBytes(inputStream);

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/*"), bytes);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

            RequestBody preset =
                    RequestBody.create(MediaType.parse("text/plain"), "ice-flame-unsign");

            RetrofitClient.getCloudinaryApi()
                    .uploadImage(body, preset)
                    .enqueue(new Callback<JsonObject>() {

                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                            binding.progressBar.setVisibility(View.GONE);

                            if (response.isSuccessful() && response.body() != null) {

                                String imageUrl = response.body()
                                        .get("secure_url").getAsString();

                                user.setProfileImageUrl(imageUrl);
                                updateUser();

                            } else {
                                Toasty.error(getContext(), "Upload failed", Toasty.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                            binding.progressBar.setVisibility(View.GONE);
                            Toasty.error(getContext(), t.getMessage(), Toasty.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            binding.progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private byte[] readBytes(InputStream inputStream) throws Exception {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];

        while ((nRead = inputStream.read(data)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    // ================= UPDATE USER =================
    private void updateUser() {

        user.setFullName(binding.etFullName.getText().toString());
        user.setMobileNumber(binding.etMobile.getText().toString());

        RetrofitClient.getUserApi()
                .updateUser(userId, user)
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        binding.progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            Toasty.success(getContext(), "Updated", Toasty.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                        binding.progressBar.setVisibility(View.GONE);
                        Toasty.error(getContext(), "Error", Toasty.LENGTH_SHORT).show();
                    }
                });
    }
}