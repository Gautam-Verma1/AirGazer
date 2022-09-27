package com.example.safebreathe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AirQualityData extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality_data);

        bottomNavigationView = findViewById(R.id.bottom_navigatin_view);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homesFragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.data, new HomesFragment()).commit();
                        return true;

                    case R.id.settingFragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.data, new SettingFragment()).commit();
                        return true;

                    case R.id.globeFragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.data, new GlobeFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}