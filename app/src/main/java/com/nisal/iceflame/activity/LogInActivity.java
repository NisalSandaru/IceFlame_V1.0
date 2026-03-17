package com.nisal.iceflame.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.nisal.iceflame.databinding.ActivityLogInBinding;
import com.nisal.iceflame.model.SignInRequest;
import com.nisal.iceflame.model.User;
import com.nisal.iceflame.network.AuthApi;
import com.nisal.iceflame.network.DeviceApi;
import com.nisal.iceflame.network.RetrofitClient;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogInActivity extends AppCompatActivity {

    private ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{
                    android.Manifest.permission.POST_NOTIFICATIONS
            }, 1);
        }

        binding.signInBtnSignIn.setOnClickListener(v->{

            String email = binding.signinInputEmail.getText().toString().trim();
            String password = binding.signinInputPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email & password required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.signinInputEmail.setError("Please enter a valid email");
                binding.signinInputEmail.requestFocus();
                return;
            }

            SignInRequest request = new SignInRequest(email, password);
            AuthApi api = RetrofitClient.getAuthApi();

            api.signIn(request).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();

                        // Save user info locally
                        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong("user_id", user.getId());
                        editor.putString("user_email", user.getEmail());
                        editor.putString("user_name", user.getFullName());
                        editor.apply();

                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(task -> {

                                    if (!task.isSuccessful()) return;

                                    String token = task.getResult();

                                    sendTokenToBackend(user.getId(), token);
                                });

                        Toasty.success(LogInActivity.this, "Welcome " + user.getFullName(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LogInActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toasty.error(LogInActivity.this, "Login failed: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toasty.error(LogInActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

    }

    private void sendTokenToBackend(Long userId, String token) {

        DeviceApi api = RetrofitClient.getDeviceApi();

        api.saveToken(userId, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                android.util.Log.d("FCM", "Token saved successfully");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                android.util.Log.e("FCM", "Failed to save token");
            }
        });
    }
}