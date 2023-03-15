package com.example.demodatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.demodatabase.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangePasswordActivity extends AppCompatActivity {


    ImageView imvBackButton;
    EditText etCurrentPassword, etPassword, reTypePassword;
    TextView tvCurrentPassword;
    FirebaseUser currentUser;
    Button imvFinishChangePassword;
    FirebaseFirestore database;
    SweetAlertDialog sweetAlertDialog,pDialog;
    boolean isGoogleAccount;


    void initUI() {
        getSupportActionBar().hide();
        imvBackButton = findViewById(R.id.imv_back);
        imvFinishChangePassword = findViewById(R.id.imv_finishChangePassword);
        etCurrentPassword = findViewById(R.id.et_currentPassword);
        etPassword = findViewById(R.id.et_password);
        reTypePassword = findViewById(R.id.et_confirmPassword);
        tvCurrentPassword = findViewById(R.id.tv_currentPassword);
        database = FirebaseFirestore.getInstance();

    }

    void initData() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        sweetAlertDialog = new SweetAlertDialog(ChangePasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setContentText("Change password successfully")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                       onBackPressed();
                    }
                });

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");

        database.collection("users")
                .document(currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        if (user.isGoogleAccount()) {
                            isGoogleAccount = true;
                            etCurrentPassword.setVisibility(View.INVISIBLE);
                            tvCurrentPassword.setVisibility(View.INVISIBLE);
                        }
                    }
                });


    }


    void bindingAction() {
        imvBackButton.setOnClickListener(view -> {
            onBackPressed();
        });

        imvFinishChangePassword.setOnClickListener(view -> {
            boolean isValidPassword = true;


            if (isGoogleAccount) {
                String newPassword = etPassword.getText().toString().trim();
                String confirmPassword = reTypePassword.getText().toString().trim();

                if (newPassword.length() < 8) {
                    etPassword.setError("Password must has at least 8 characters");
                    isValidPassword = false;
                }
                if (!confirmPassword.equals(newPassword)) {
                    reTypePassword.setError("Re-type password must match with new password");
                    isValidPassword = false;
                }
                if (isValidPassword) {
                    pDialog.show();
                    currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.collection("users")
                                    .document(currentUser.getEmail())
                                    .update("googleAccount", false, "password", newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pDialog.cancel();
                                            sweetAlertDialog.show();
                                        }
                                    });
                        }
                    });
                }
            } else {
                String newPassword = etPassword.getText().toString().trim();
                String confirmPassword = reTypePassword.getText().toString().trim();
                String currentPassword = etCurrentPassword.getText().toString().trim();

                if (currentPassword.length() < 8) {
                    etCurrentPassword.setError("You must filled in the current password");
                    isValidPassword = false;
                }

                if (newPassword.length() < 8) {
                    etPassword.setError("Password must has at least 8 characters");
                    isValidPassword = false;
                }
                if (!confirmPassword.equals(newPassword)) {
                    reTypePassword.setError("Re-type password must match with new password");
                    isValidPassword = false;
                }

                if (isValidPassword) {

                    pDialog.show();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), currentPassword.toString());
                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    database.collection("users")
                                                            .document(currentUser.getEmail())
                                                            .update("googleAccount", false, "password", newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    pDialog.cancel();
                                                                    sweetAlertDialog.show();
                                                                }
                                                            });
                                                  
                                                }
                                            }
                                        });
                                    }else {
                                        pDialog.cancel();
                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                        sweetAlertDialog.setContentText("Wrong current password please try again")
                                                .show();
                                    }
                                }
                            });

                }
            }
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