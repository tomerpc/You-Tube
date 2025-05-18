
package com.example.aspp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.aspp.fragments.AddVideoFragment;
import com.example.aspp.fragments.ShortsFragment;
import com.example.aspp.fragments.HomeFragment;
import com.example.aspp.fragments.ProfileFragment;
import com.example.aspp.databinding.ActivityMainBinding;
import com.example.aspp.fragments.SignInFragment;
import com.example.aspp.fragments.SignInRequestFragment;
import com.example.aspp.fragments.SubscriptionsFragment;
import com.example.aspp.entities.User;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private User myUser = Helper.getSignedInUser();
    boolean signIn = Helper.isSignedIn();
    ActivityMainBinding binding;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    boolean nightMode;
    SharedPreferences sp;
    private Fragment currentFragment;
    private static final int REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }



        sp = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sp.getBoolean("night",false);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar activtytoolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, activtytoolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        drawerLayout.closeDrawer(GravityCompat.START);
        navigationView = findViewById(R.id.nav_view);

        binding.bottomNavbar.getMenu().findItem(R.id.home).setChecked(true);


        //Handle the login state - gets the user object and handle the login-logout on the side navigate item.
        User loggedInUser = (User) getIntent().getSerializableExtra("loggedInUser");
        // Log the retrieved user for debugging
        if (loggedInUser != null) {
            Log.d("MainActivity", "Logged in user: " + loggedInUser.getUsername());
            myUser = loggedInUser;
            signIn = true;
        } else {
            Log.d("MainActivity", "No user logged in.");
        }

        //handle the login-logout
        MenuItem loginLogoutItem = navigationView.getMenu().findItem(R.id.logout);
        if (signIn) {
            loginLogoutItem.setTitle("Logout");
        } else {
            loginLogoutItem.setTitle("Login");
        }


        switchFragment(new HomeFragment(myUser));
        currentFragment = new HomeFragment(myUser);

        if (nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            navigationView.getMenu().getItem(2).setTitle("Light mode");
            navigationView.getMenu().getItem(2).setIcon(R.drawable.baseline_light_mode_24);
            switchFragment(currentFragment);
        }

        binding.bottomNavbar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home){
                switchFragment(new HomeFragment(nightMode, myUser));
                currentFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.shorts) {
                switchFragment(new ShortsFragment());
                currentFragment = new ShortsFragment();
            }
            else if (item.getItemId() == R.id.subscriptions) {
                switchFragment(new SubscriptionsFragment());
                currentFragment = new SubscriptionsFragment();
            }
            else if (item.getItemId() == R.id.add_video) {
                if(!signIn){
                    switchFragment(new SignInRequestFragment());
                    currentFragment = new SignInRequestFragment();
                }
                else {
                    switchFragment(new AddVideoFragment(myUser));
                    currentFragment = new AddVideoFragment();
                }
            }
            else {
                if(!signIn){
                    switchFragment(new SignInRequestFragment());
                    currentFragment = new SignInRequestFragment();
                }
                else {
                    switchFragment(new ProfileFragment());
                    currentFragment = new ProfileFragment();
                }
            }
            return true;
        });

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i("MODE","on");
                if (item.getItemId() == R.id.liked_videos) {
                    //go to liked videos playlist
                }
                else if (item.getItemId() == R.id.watch_later) {
                    //go to watch later playlist
                }
                else if (item.getItemId() == R.id.mode) {
                    Log.i("MODE","on");
                    if (nightMode) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        sp.edit().putBoolean("night",false).apply();
                        item.setTitle("Light mode");
                        item.setIcon(R.drawable.baseline_light_mode_24);
                        switchFragment(currentFragment);
                    }
                    else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        sp.edit().putBoolean("night",true).apply();
                        item.setTitle("Dark mode");
                        item.setIcon(R.drawable.baseline_dark_mode_24);
                        switchFragment(currentFragment);
                    }
                }
                else {
                    if(signIn){
                        myUser = null;
                        signIn = false;
                        item.setTitle("Login");
                    }
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                }
                return false;
            }
        });
    }

    public void switchFragment(androidx.fragment.app.Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragmentContainer, f, null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        File file = new File(getFilesDir(), "user_credentials.json");
        //close the users files to make it not persist
        if(file.exists()){
            file.delete();
        }
        finishAffinity();

    }

    public void setFragment(Fragment newFragment) {
        this.currentFragment = newFragment;
        switchFragment(currentFragment);
    }
}
