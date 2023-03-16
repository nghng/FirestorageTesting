package com.example.demodatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText et_resetGmail;
    Button btn_resetPassword;
    FirebaseAuth auth;
    private TextView tv_SignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password);
        init();
        bindingEvents();
    }

    void init() {
        et_resetGmail = findViewById(R.id.et_resetGmail);
        btn_resetPassword = findViewById(R.id.btn_resetPassword);
        tv_SignIn = findViewById(R.id.tv_toLogIn);
        auth = FirebaseAuth.getInstance();
    }

    private void bindingEvents() {
        btn_resetPassword.setOnClickListener(view -> {
            resetPasswordByEmail();
        });
        tv_SignIn.setOnClickListener(view -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void resetPasswordByEmail() {
        String email = et_resetGmail.getText().toString().trim();
        if (email.isEmpty()) {
            et_resetGmail.setError("Email is required");
            et_resetGmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_resetGmail.setError("Please enter valid email");
            et_resetGmail.requestFocus();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(ForgotPasswordActivity.this, CheckMailActivity.class);
                    intent.putExtra("msg","Please check your email to change your password");
                    startActivity(intent);
                } else {
                    FancyToast.makeText(ForgotPasswordActivity.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });
    }
}