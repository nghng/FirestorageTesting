package com.example.demodatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.nullness.qual.NonNull;

public class LoginActivity extends AppCompatActivity {
    EditText editText_gmail;
    EditText editText_password;
    Button btn_login;
    TextView textView_signUpLink;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    void init(){
        editText_gmail = findViewById(R.id.editText_gmail);
        editText_password = findViewById(R.id.editText_password);
        btn_login = findViewById(R.id.btn_login);
        textView_signUpLink = findViewById(R.id.textView_signUpLink);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gmail = editText_gmail.getText().toString().trim();
                String password = editText_password.getText().toString().trim();

                firebaseAuth = FirebaseAuth.getInstance();
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(gmail, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }
}