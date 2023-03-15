package com.example.demodatabase.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.demodatabase.R;
import com.example.demodatabase.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class AdminProfileFrament extends Fragment {
    FirebaseUser admin;
    ImageView imvAccountImage;
    TextView email, displayName;
    FirebaseFirestore database;



    public AdminProfileFrament() {
    }

    void initUI(View view){
        admin = FirebaseAuth.getInstance().getCurrentUser();
        imvAccountImage = view.findViewById(R.id.imv_accountImage);
        email = view.findViewById(R.id.tv_email);
        displayName = view.findViewById(R.id.tv_displayName);

    }

    void initData(){
        database = FirebaseFirestore.getInstance();
        database.collection("users")
                .document(admin.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        Uri photoURL = admin.getPhotoUrl();
                        Glide.with(getContext()).load(photoURL).error(R.drawable.default_user_image)
                                .into(imvAccountImage);
                        email.setText(admin.getEmail());
                        displayName.setText(user.getDisplayName());
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_profile_frament, container, false);
        initUI(view);
        initData();
        return view;
    }
}