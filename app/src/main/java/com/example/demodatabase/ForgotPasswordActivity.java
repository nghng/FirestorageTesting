package com.example.demodatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText et_resetGmail;
    Button btn_resetPassword;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
        bindingEvents();
    }

    void init(){
        et_resetGmail = findViewById(R.id.et_resetGmail);
        btn_resetPassword = findViewById(R.id.btn_resetPassword);
        auth = FirebaseAuth.getInstance();
    }

    private void bindingEvents(){
        btn_resetPassword.setOnClickListener(view -> {
            resetPasswordByEmail();
        });
    }

    private void resetPasswordByEmail() {
        String email = et_resetGmail.getText().toString().trim();
        if(email.isEmpty()){
            et_resetGmail.setError("Email is required");
            et_resetGmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_resetGmail.setError("Please enter valid email");
            et_resetGmail.requestFocus();
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(ForgotPasswordActivity.this,CheckMailActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(ForgotPasswordActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}