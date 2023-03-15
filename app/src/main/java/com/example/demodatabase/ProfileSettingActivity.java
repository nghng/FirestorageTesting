package com.example.demodatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileSettingActivity extends AppCompatActivity {
    FirebaseUser user;
    TextView tvEmail;
    TextView tvUsernameInSetting;
    ImageView ivBackButton;
    Button btnLogout, btnChangePassword;
    private FirebaseAuth firebaseAuth;



    void init(){
        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        tvEmail = findViewById(R.id.tv_email);
        tvUsernameInSetting = findViewById(R.id.tv_usernameInSetting);
        ivBackButton= findViewById(R.id.iv_backButton);
        btnLogout = findViewById(R.id.btn_logout);
        btnChangePassword = findViewById(R.id.btn_changePassword);
    }

    void initData(){
        tvEmail.setText(user.getEmail());
        tvUsernameInSetting.setText(user.getDisplayName());
    }

    void bindingAction(){
        btnLogout.setOnClickListener(view -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(ProfileSettingActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        ivBackButton.setOnClickListener(view -> {
           onBackPressed();
        });

        btnChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileSettingActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        init();
        initData();
        bindingAction();
    }
}