package com.example.demodatabase.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.demodatabase.ChangePasswordActivity;
import com.example.demodatabase.R;
import com.example.demodatabase.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class AdminProfileFrament extends Fragment {
    FirebaseUser admin;
    ImageView imvAccountImage;
    TextView email, displayName;
    FirebaseFirestore database;
    Button btnChangePassword;



    public AdminProfileFrament() {
    }

    void initUI(View view){
        admin = FirebaseAuth.getInstance().getCurrentUser();
        imvAccountImage = view.findViewById(R.id.imv_accountImage);
        email = view.findViewById(R.id.tv_email);
        displayName = view.findViewById(R.id.tv_displayName);
        btnChangePassword = view.findViewById(R.id.btn_changePassword);

    }

    void initData(){
        database = FirebaseFirestore.getInstance();
        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        database.collection("users")
                .document(admin.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        Uri photoURL = Uri.parse(user.getImageUri());
                        Glide.with(getContext()).load(photoURL).error(R.drawable.default_user_image)
                                .into(imvAccountImage);
                        email.append(admin.getEmail());
                        displayName.append(user.getDisplayName());
                        pDialog.cancel();
                    }
                });

    }

    void bindingAction(){
        btnChangePassword.setOnClickListener(view ->{
            startActivity(new Intent(getContext(), ChangePasswordActivity.class));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_profile_frament, container, false);
        initUI(view);
        initData();
        bindingAction();
        return view;
    }
}