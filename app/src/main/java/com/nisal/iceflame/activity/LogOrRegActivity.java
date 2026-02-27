package com.nisal.iceflame.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nisal.iceflame.databinding.ActivityLogOrRegBinding;

public class LogOrRegActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLogOrRegBinding binding = ActivityLogOrRegBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logOrRegBtn.setOnClickListener(view->{
            Intent intent = new Intent(LogOrRegActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}