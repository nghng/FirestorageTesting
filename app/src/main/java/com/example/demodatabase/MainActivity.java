package com.example.demodatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.demodatabase.databinding.ActivityMainBinding;
import com.example.demodatabase.fragments.AddStudySetFragment;
import com.example.demodatabase.fragments.HomeFragment;
import com.example.demodatabase.fragments.ProfileFragment;
import com.example.demodatabase.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;
    ConstraintLayout wrapper, addingOption;
    TextView textViewCreateStudySet, createFolder;
    BottomNavigationView bottomNavigationView;



    private void initData() {
        activityMainBinding.wrapper.setVisibility(View.GONE);
    }

    void bindingAction(){
        activityMainBinding.conLayOptOut.setOnClickListener(view -> {
            activityMainBinding.bottomNavigation.setVisibility(View.VISIBLE);
            activityMainBinding.wrapper.setVisibility(View.GONE);

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        replaceFragment(new HomeFragment());
        initData();
        bindingAction();



        activityMainBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;

                case R.id.addStudySet:
                    activityMainBinding.wrapper.setVisibility(View.VISIBLE);
                    activityMainBinding.bottomNavigation.setVisibility(View.GONE);
                    activityMainBinding.wrapper.bringToFront();
                    break;

                case R.id.search:

                    replaceFragment(new SearchFragment());
                    break;

                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return true;
        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}