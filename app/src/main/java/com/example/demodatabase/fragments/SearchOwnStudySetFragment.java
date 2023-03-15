package com.example.demodatabase.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.demodatabase.CreateStudySetActivity;
import com.example.demodatabase.MainActivity;
import com.example.demodatabase.R;
import com.example.demodatabase.StudySetDetailActivity;
import com.example.demodatabase.adapter.StudySetAdapter;
import com.example.demodatabase.clickinterface.OnItemClickedListener;
import com.example.demodatabase.model.StudySet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SearchOwnStudySetFragment extends Fragment {
    ImageView btnBack, btnAddSet;
    Button btnSearchAll, btnSearchCreated, btnSearchStudied;
    EditText etSearch;
    FirebaseFirestore database;
    FirebaseUser currentUser;
    ArrayList<StudySet> studySets = new ArrayList<>();
    RecyclerView rcStudySets;
    StudySetAdapter studySetAdapter;

    void initUI(View view){
        btnBack = view.findViewById(R.id.btn_back);
        btnAddSet = view.findViewById(R.id.btn_addSet);
        btnSearchAll = view.findViewById(R.id.btn_searchAll);
        btnSearchCreated = view.findViewById(R.id.btn_searchCreatedStudySet);
        btnSearchStudied = view.findViewById(R.id.btn_searchStudiedStudySet);
        etSearch = view.findViewById(R.id.et_search);
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rcStudySets = view.findViewById(R.id.rv_studySets);
    }

    void initData(){

    }

    void onDataLoaded(){
        studySetAdapter = new StudySetAdapter(studySets, getContext(), new OnItemClickedListener() {
            @Override
            public void onItemClick(StudySet item, int pos) {
                Intent intent = new Intent(getContext(), StudySetDetailActivity.class);
                intent.putExtra("studySetID", item.getStudySetID());
                startActivity(intent);;
            }
        });

        rcStudySets.setLayoutManager(new LinearLayoutManager(getContext()));
        rcStudySets.setAdapter(studySetAdapter);
    }

    void getAllData(){
        studySets.clear();
        database.collection("studySets")
                .whereEqualTo("user" , currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot d : task.getResult()
                             ) {
                                StudySet studySet = d.toObject(StudySet.class);
                                studySet.setStudySetID(d.getId());
                                studySets.add(studySet);
                        }
                        onDataLoaded();
                    }
                });
    }

    void bindingAction(){

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSearchAll.setOnClickListener(v -> {
            etSearch.setVisibility(View.VISIBLE);
            btnSearchAll.setBackgroundColor(Color.BLACK);
            btnSearchStudied.setBackgroundColor(Color.parseColor("#38B6FF"));
            btnSearchCreated.setBackgroundColor(Color.parseColor("#38B6FF"));
            getAllData();
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