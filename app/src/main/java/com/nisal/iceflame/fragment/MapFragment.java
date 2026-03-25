package com.nisal.iceflame.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.nisal.iceflame.R;
import com.nisal.iceflame.databinding.FragmentMapBinding;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private GoogleMap mMap;

    public MapFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMapBinding.inflate(inflater, container, false);

        // 🔥 IMPORTANT: use R.id.map (NOT binding.map)
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return binding.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // 📍 Example Location (Colombo)
        LatLng colombo = new LatLng(6.9271, 79.8612);

        // Add marker
        mMap.addMarker(new MarkerOptions()
                .position(colombo)
                .title("Restaurant Location"));

        // Move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo, 12));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}