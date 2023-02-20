package com.example.demodatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.demodatabase.databinding.ActivityMainBinding;
import com.example.demodatabase.fragments.AddStudySetFragment;
import com.example.demodatabase.fragments.HomeFragment;
import com.example.demodatabase.fragments.ProfileFragment;
import com.example.demodatabase.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        replaceFragment(new HomeFragment());

        activityMainBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;

                case R.id.addStudySet:
                    replaceFragment(new AddStudySetFragment());
                    break;

                case R.id.search:
                    replaceFragment(new SearchFragment());
                    break;

                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return  true;
        });


    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}