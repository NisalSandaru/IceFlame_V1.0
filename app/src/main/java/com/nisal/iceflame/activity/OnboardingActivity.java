package com.nisal.iceflame.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.nisal.iceflame.R;
import com.nisal.iceflame.adapters.OnboardingAdapter;

import java.util.Arrays;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext, btnSkip;
    private OnboardingAdapter adapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.onboardingViewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        dotsLayout = findViewById(R.id.dotsLayout);

        // 3 pages
        List<Integer> images = Arrays.asList(
                R.drawable.chef,
                R.drawable.burger
        );

        List<String> titles = Arrays.asList(
                "Welcome to IceFlame App",
                "Explore Delicious Cuisines"
        );

        List<String> texts = Arrays.asList(
                "Discover a world of bold flavors, sweet treats, and unforgettable tastes crafted just for you.",
                "Browse a wide variety of mouth-watering dishes and find your favorites in just a few taps."
        );

        adapter = new OnboardingAdapter(images, titles, texts);
        viewPager.setAdapter(adapter);

        // AFTER setting the adapter
        viewPager.post(() -> addDotsIndicator(viewPager.getCurrentItem()));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);
            }
        });

        btnNext.setOnClickListener(v -> {
            int next = viewPager.getCurrentItem() + 1;
            if (next < adapter.getItemCount()) {
                viewPager.setCurrentItem(next);
            } else {
                finishOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    // Add dots method
    private void addDotsIndicator(int position) {
        dotsLayout.removeAllViews();
        dots = new TextView[adapter.getItemCount()];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
            dots[i].setTextColor(Color.parseColor("#888888")); // inactive color
            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0)
            dots[position].setTextColor(Color.parseColor("#FFFFFF")); // active color
    }

    private void finishOnboarding() {
        startActivity(new Intent(this, LogOrRegActivity.class));
        finish();
    }
}