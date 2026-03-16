package com.nisal.iceflame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nisal.iceflame.R;
import com.nisal.iceflame.databinding.ActivityMainBinding;
import com.nisal.iceflame.databinding.SideNavHeaderBinding;
import com.nisal.iceflame.fragment.CartFragment;
import com.nisal.iceflame.fragment.ExploreFragment;
import com.nisal.iceflame.fragment.HomeFragment;
import com.nisal.iceflame.fragment.ProfileFragment;
import com.nisal.iceflame.fragment.SettingFragment;
import com.nisal.iceflame.fragment.WishlistFragment;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener{

    private ActivityMainBinding binding;
    private SideNavHeaderBinding sideNavHeaderBinding;
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View headerView = binding.sideNavigationView.getHeaderView(0);
        sideNavHeaderBinding = SideNavHeaderBinding.bind(headerView);


        drawerLayout = binding.drawerlayout;
        toolbar = binding.toolbar;
        navigationView = binding.sideNavigationView;
        bottomNavigationView = binding.bottomNavigationView;

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) return;

                    String token = task.getResult();

                    Log.d("FCM_TOKEN", token);
                    Toasty.success(this,"Please login first", Toast.LENGTH_SHORT).show();
                });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{
                    android.Manifest.permission.POST_NOTIFICATIONS
            }, 1);
        }

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

//        toolbar.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.action_settings) {
//                startActivity(new Intent(this, ProfileFragment.class));
//                return true;
//            }
//            return false;
//        });

//        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                }else {
//                    finish();
//                }
//            }
//        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }

                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.getMenu().findItem(R.id.side_nav_home).setChecked(true);
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        Menu navMenu = navigationView.getMenu();
        Menu bottonNavMenu = bottomNavigationView.getMenu();

        for (int i=0; i<navMenu.size(); i++){
            navMenu.getItem(i).setChecked(false);
        }

        for (int i = 0; i < bottonNavMenu.size(); i++){
            bottonNavMenu.getItem(i).setChecked(false);
        }

        if (itemId == R.id.side_nav_home || itemId == R.id.bottom_nav_home){
            loadFragment(new HomeFragment());
            navigationView.getMenu().findItem(R.id.side_nav_home).setChecked(true);
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);
        } else if (itemId == R.id.bottom_nav_category) {
            loadFragment(new ExploreFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_category).setChecked(true);
        } else if (itemId == R.id.bottom_nav_cart) {
            loadFragment(new CartFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_cart).setChecked(true);
        } else if (itemId == R.id.bottom_nav_watchlist) {
            loadFragment(new WishlistFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_watchlist).setChecked(true);
        } else if (itemId == R.id.bottom_nav_setting) {
            loadFragment(new SettingFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_setting).setChecked(true);
        }
        return true;
    }

//    private void loadFragment(Fragment fragment){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.fragment_container, fragment);
//        transaction.commit();
//
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
//
//    }

    private void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}