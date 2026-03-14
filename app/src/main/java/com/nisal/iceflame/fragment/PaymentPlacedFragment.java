package com.nisal.iceflame.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisal.iceflame.R;
import com.nisal.iceflame.activity.MainActivity;
import com.nisal.iceflame.activity.SplashActivity;
import com.nisal.iceflame.databinding.FragmentCheckoutBinding;
import com.nisal.iceflame.databinding.FragmentPaymentPlacedBinding;

public class PaymentPlacedFragment extends Fragment {

    private FragmentPaymentPlacedBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaymentPlacedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnContinue.setOnClickListener(v -> {

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MyOrdersFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}