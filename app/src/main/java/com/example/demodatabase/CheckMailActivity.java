package com.example.demodatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CheckMailActivity extends AppCompatActivity {
    TextView tv_toLogin, tvMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_check_mail);
        init();
        bindingEvents();
    }

    void init(){
        tv_toLogin = findViewById(R.id.tv_toLoginFromCheckingMail);
        tvMessage = findViewById(R.id.tvMessage);
    }

    void bindingEvents(){
        tv_toLogin.setOnClickListener(view -> {
            Intent intent = new Intent(CheckMailActivity.this, LoginActivity.class);
            startActivity(intent);
        });
       tvMessage.setText(getIntent().getExtras().getString("msg"));
    }
}