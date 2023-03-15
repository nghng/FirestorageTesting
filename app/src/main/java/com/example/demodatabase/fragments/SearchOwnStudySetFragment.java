package com.example.demodatabase.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.demodatabase.CreateStudySetActivity;
import com.example.demodatabase.MainActivity;
import com.example.demodatabase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class SearchOwnStudySetFragment extends Fragment {
    ImageView btnBack, btnAddSet;
    Button btnSearchAll, btnSearchCreated, btnSearchStudied;
    EditText etSearch;
    FirebaseFirestore database;
    FirebaseUser currentUser;

    void initUI(View view){
        btnBack = view.findViewById(R.id.btn_back);
        btnAddSet = view.findViewById(R.id.btn_addSet);
        btnSearchAll = view.findViewById(R.id.btn_searchAll);
        btnSearchCreated = view.findViewById(R.id.btn_searchCreatedStudySet);
        btnSearchStudied = view.findViewById(R.id.btn_searchStudiedStudySet);
        etSearch = view.findViewById(R.id.et_search);
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    void initData(){

    }
    void bindingAction(){

        btnSearchAll.setOnClickListener(v -> {
            etSearch.setVisibility(View.VISIBLE);
            btnSearchAll.setBackgroundColor(Color.BLACK);
            btnSearchStudied.setBackgroundColor(Color.parseColor("#38B6FF"));
            btnSearchCreated.setBackgroundColor(Color.parseColor("#38B6FF"));
        });

        btnSearchStudied.setOnClickListener(v -> {
            etSearch.setVisibility(View.INVISIBLE);
            btnSearchStudied.setBackgroundColor(Color.BLACK);
            btnSearchAll.setBackgroundColor(Color.parseColor("#38B6FF"));
            btnSearchCreated.setBackgroundColor(Color.parseColor("#38B6FF"));
        });

        btnSearchCreated.setOnClickListener(v -> {
            etSearch.setVisibility(View.INVISIBLE);
            btnSearchCreated.setBackgroundColor(Color.BLACK);
            btnSearchAll.setBackgroundColor(Color.parseColor("#38B6FF"));
            btnSearchStudied.setBackgroundColor(Color.parseColor("#38B6FF"));
        });

        btnBack.setOnClickListener(view ->{
            ((MainActivity)getActivity()).replaceFragment(new HomeFragment());
        });

        btnAddSet.setOnClickListener(view ->{
            startActivity(new Intent(getContext(), CreateStudySetActivity.class));
        });

        btnSearchAll.performClick();
    }

    public SearchOwnStudySetFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_own_study_set, container, false);
        initUI(view);
        initData();
        bindingAction();

        return view;
    }
}