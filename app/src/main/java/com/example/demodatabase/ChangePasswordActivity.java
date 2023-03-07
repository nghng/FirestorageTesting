package com.example.demodatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class ChangePasswordActivity extends AppCompatActivity {


    ImageView imvBackButton;
    EditText etCurrentPassword, etPassword, reTypePassword;
    FirebaseUser currentUser;
    Button imvFinishChangePassword;

    void initUI(){
        imvBackButton = findViewById(R.id.imv_back);
        imvFinishChangePassword = findViewById(R.id.imv_finishChangePassword);
        etCurrentPassword = findViewById(R.id.et_currentPassword);
        etPassword = findViewById(R.id.et_password);
        reTypePassword = findViewById(R.id.et_confirmPassword);
    }

    void initData(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }
    void bindingAction(){
        imvBackButton.setOnClickListener(view -> {
            onBackPressed();
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_change_password);
        initUI();
        initData();
        bindingAction();
    }
}