package com.nisal.iceflame.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nisal.iceflame.R;

public class SplashActivity extends AppCompatActivity {

    private static final long MIN_SPLASH_TIME = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView = findViewById(R.id.splashLogo);

        Glide.with(this)
                .asBitmap()
                .load(R.drawable.logo_no_bg_ice_flame)
                .into(imageView);

        long startTime = System.currentTimeMillis();

        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getString("user_email", null) != null;

        // Decide next activity
        Runnable navigateRunnable = () -> {
            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            }
            startActivity(intent);
            finish();
        };

        // Ensure minimum splash time
        long elapsed = System.currentTimeMillis() - startTime;
        long delay = Math.max(0, MIN_SPLASH_TIME - elapsed);

        new Handler(Looper.getMainLooper()).postDelayed(navigateRunnable, delay);
    }
}