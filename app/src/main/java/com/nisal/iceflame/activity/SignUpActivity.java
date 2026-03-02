package com.nisal.iceflame.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nisal.iceflame.databinding.ActivitySignUpBinding;

import android.widget.Toast;

import com.nisal.iceflame.model.SignUpRequest;
import com.nisal.iceflame.network.AuthApi;
import com.nisal.iceflame.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUpBtnSignUp.setOnClickListener(v->{

            String name = binding.signUpInputName.getText().toString().trim();
            String email = binding.signUpInputEmail.getText().toString().trim();
            String password = binding.signUpInputPassword.getText().toString().trim();
            String retypePassword = binding.signUpInputRetypePassword.getText().toString().trim();

            if (name.isEmpty()){
                binding.signUpInputName.setError("Name is required");
                binding.signUpInputName.requestFocus();
                return;
            }

            if (email.isEmpty()){
                binding.signUpInputEmail.setError("Email is required");
                binding.signUpInputEmail.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.signUpInputEmail.setError("Please enter a valid email");
                binding.signUpInputEmail.requestFocus();
                return;
            }

            if (password.isEmpty()){
                binding.signUpInputPassword.setError("Password is required");
                binding.signUpInputPassword.requestFocus();
                return;
            }

            if (password.length() < 6){
                binding.signUpInputPassword.setError("Password must be over 6 characters");
                binding.signUpInputPassword.requestFocus();
                return;
            }

            if (!password.equals(retypePassword)){
                binding.signUpInputRetypePassword.setError("Password does not match");
                binding.signUpInputRetypePassword.requestFocus();
                return;
            }

            SignUpRequest request =
                    new SignUpRequest(name, email, password);

            AuthApi api = RetrofitClient.getAuthApi();

            api.signUp(request).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(SignUpActivity.this,
                                "Signup successful",
                                Toast.LENGTH_SHORT).show();

                        Intent intent =
                                new Intent(SignUpActivity.this, LogInActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        try {
                            String error = response.errorBody().string();
                            Toast.makeText(SignUpActivity.this,
                                    error,
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this,
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                    System.out.println("Network error: " + t.getMessage());
                }
            });
        });

        binding.btnGoToLogIn.setOnClickListener(v->{
            Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}