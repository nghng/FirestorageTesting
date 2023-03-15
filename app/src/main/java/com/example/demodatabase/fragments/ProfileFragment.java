package com.example.demodatabase.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.demodatabase.ProfileSettingActivity;
import com.example.demodatabase.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {
    TextView textViewUsername;
    ImageView imageViewProfileImage;
    FirebaseUser currentUser;
    BottomNavigationView bottomNavigationView;
    ImageView settingButton;
    RelativeLayout header;
    int markedTab = 0;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(int markedTab) {
        this.markedTab = markedTab;
    }

    void init(View view) {
        textViewUsername = view.findViewById(R.id.tv_username);
        imageViewProfileImage = view.findViewById(R.id.iv_profileImage);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        bottomNavigationView = view.findViewById(R.id.bottomNavProfile);
        settingButton = view.findViewById(R.id.iv_settingButton);
    }

    void initData() {

        if (currentUser == null) {
            return;
        }
        String username = currentUser.getDisplayName();
        Uri photoURL = currentUser.getPhotoUrl();
        Glide.with(this).load(photoURL).error(R.drawable.default_user_image)
                .into(imageViewProfileImage);
        textViewUsername.setText(username);

    }

    void bindingAction() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        if (markedTab == 0) {
            replaceFragment(new ProfileSetFragment());

            bottomNavigationView.setOnItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.profileSets:
                        replaceFragment(new ProfileSetFragment());
                        break;

                    case R.id.profileClass:
                        replaceFragment(new ProfileClassFragment());
                        break;

                    case R.id.profileFolders:
                        replaceFragment(new ProfileFolderFragment());
                        break;
                }
                return true;
            });
        } else {
            bottomNavigationView.setSelectedItemId(R.id.profileFolders);
            replaceFragment(new ProfileFolderFragment());
            markedTab = 0;
        }


        settingButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ProfileSettingActivity.class);
            startActivity(intent);
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        initData();
        bindingAction();


        return view;
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.switchMenuProfile, fragment).commit();

    }
}