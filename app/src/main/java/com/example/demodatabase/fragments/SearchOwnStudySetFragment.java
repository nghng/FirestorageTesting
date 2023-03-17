package com.example.demodatabase.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.demodatabase.model.Learn;
import com.example.demodatabase.model.StudySet;
import com.example.demodatabase.model.Term;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SearchOwnStudySetFragment extends Fragment {
    ImageView btnBack, btnAddSet;
    Button btnSearchAll, btnSearchCreated, btnSearchStudied;
    EditText etSearch;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ;
    FirebaseUser currentUser;
    ArrayList<StudySet> studySets = new ArrayList<>();
    RecyclerView rcStudySets;
    StudySetAdapter studySetAdapter;
    ProgressDialog progressDialog;
    LinkedHashMap<String, StudySet> studySetHashMap = new LinkedHashMap<>();
    CollectionReference studySetRef = database.collection("studySets");
    CollectionReference refLearn = database.collection("learn");


    void initUI(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnAddSet = view.findViewById(R.id.btn_addSet);
        btnSearchAll = view.findViewById(R.id.btn_searchAll);
        btnSearchCreated = view.findViewById(R.id.btn_searchCreatedStudySet);
        btnSearchStudied = view.findViewById(R.id.btn_searchStudiedStudySet);
        etSearch = view.findViewById(R.id.et_search);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rcStudySets = view.findViewById(R.id.rv_studySets);
        progressDialog = new ProgressDialog(getContext());


    }

    void initData() {

    }


    void onDataLoaded() {
        studySets.clear();
        for (String s : studySetHashMap.keySet()
        ) {
            studySets.add(studySetHashMap.get(s));
        }
        studySetAdapter = new StudySetAdapter(studySets, getContext(), new OnItemClickedListener() {
            @Override
            public void onItemClick(StudySet item, int pos) {
                Intent intent = new Intent(getContext(), StudySetDetailActivity.class);
                intent.putExtra("studySetID", item.getStudySetID());
                startActivity(intent);
                ;
            }
        });

        rcStudySets.setLayoutManager(new LinearLayoutManager(getContext()));
        rcStudySets.setAdapter(studySetAdapter);
    }

    void getAllData() {
        studySetHashMap.clear();
        if(studySetAdapter != null){
            studySets.clear();
            studySetAdapter.notifyDataSetChanged();
        }
        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        studySetRef.whereEqualTo("user", currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            pDialog.cancel();
                            return;

                        }
                        for (DocumentSnapshot d : task.getResult()
                        ) {
                            StudySet studySet = d.toObject(StudySet.class);
                            studySet.setStudySetID(d.getId());
                            database.collection("studySets")
                                    .document(d.getId())
                                    .collection("terms")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.getResult().isEmpty()) {
                                                pDialog.cancel();
                                                return;
                                            }
                                            ArrayList<Term> terms = new ArrayList<>();
                                            for (DocumentSnapshot d : task.getResult()
                                            ) {
                                                Term term = d.toObject(Term.class);
                                                terms.add(term);
                                            }
                                            studySet.setTerms(terms);
                                            studySetHashMap.put(studySet.getStudySetID(), studySet);
                                        }
                                    });
                        }
                    }
                });
        refLearn.whereEqualTo("userEmail", currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            return;
                        }
                        for (DocumentSnapshot d : task.getResult()
                        ) {
                            Learn learn = d.toObject(Learn.class);
                            if (!studySetHashMap.containsKey(learn.getStudySetID())) {
                                studySetRef.document(learn.getStudySetID())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (!task.getResult().exists()) {
                                                    pDialog.cancel();
                                                    return;
                                                }
                                                StudySet studySet = task.getResult().toObject(StudySet.class);
                                                studySet.setStudySetID(task.getResult().getId());
                                                studySetRef.document(studySet.getStudySetID())
                                                        .collection("terms")
                                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                                ArrayList<Term> terms = new ArrayList<>();
                                                                Log.d("term", "onComplete: " + task1.getResult().getDocuments().toString());
                                                                for (DocumentSnapshot d : task1.getResult()
                                                                ) {
                                                                    Term term = d.toObject(Term.class);
                                                                    terms.add(term);
                                                                }
                                                                studySet.setTerms(terms);
                                                                studySetHashMap.put(learn.getStudySetID(), studySet);
                                                                onDataLoaded();
                                                                pDialog.cancel();

                                                            }
                                                        });
                                            }
                                        });
                            }
                        }

                    }

                });


//        studySetHashMap.clear();
//        Log.d("hash", "getAllData: " + studySetHashMap.size());
//        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
//        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//        pDialog.setTitleText("Loading");
//        pDialog.setCancelable(false);
//        pDialog.show();
//
//        database.collection("studySets")
//                .whereEqualTo("user", currentUser.getEmail())
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.getResult().isEmpty()){
//                            pDialog.cancel();
//                        }
//                        for (DocumentSnapshot d : task.getResult()
//                        ) {
//                            StudySet studySet = d.toObject(StudySet.class);
//                            studySet.setStudySetID(d.getId());
//                            studySetHashMap.put(d.getId(), studySet);
//                            database.collection("studySets")
//                                    .document(d.getId())
//                                    .collection("terms")
//                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                            ArrayList<Term> terms = new ArrayList<>();
//                                            for (DocumentSnapshot d : task.getResult()
//                                            ) {
//                                                Term term = d.toObject(Term.class);
//                                                terms.add(term);
//                                            }
//                                            studySet.setTerms(terms);
//
//                                        }
//                                    });
//                        }
//
//                        database.collection("learn")
//                                .whereEqualTo("userEmail", currentUser.getEmail())
//                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        if(task.getResult().isEmpty()){
//                                            pDialog.cancel();
//
//
//                                        }
//                                        for (DocumentSnapshot d : task.getResult()
//                                        ) {
//                                            Learn learn = d.toObject(Learn.class);
//                                            if (!studySetHashMap.containsKey(learn.getStudySetID())) {
//                                                database.collection("studySets")
//                                                        .document(learn.getStudySetID())
//                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                if(!task.getResult().exists()){
//                                                                    pDialog.cancel();
//                                                                    return;
//                                                                }
//                                                                StudySet studySet = task.getResult().toObject(StudySet.class);
//                                                                studySet.setStudySetID(task.getResult().getId());
//                                                                database.collection("studySets")
//                                                                        .document(task.getResult().getId())
//                                                                        .collection("terms")
//                                                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                ArrayList<Term> terms = new ArrayList<>();
//                                                                                for (DocumentSnapshot d : task.getResult()
//                                                                                ) {
//                                                                                    Term term = d.toObject(Term.class);
//                                                                                    terms.add(term);
//                                                                                }
//                                                                                studySet.setTerms(terms);
//                                                                                studySetHashMap.put(studySet.getStudySetID(), studySet);
//                                                                                pDialog.cancel();
//
//
//                                                                            }
//                                                                        });
//
//                                                            }
//                                                        });
//                                            }
//                                        }
//                                        onDataLoaded();
//
//                                    }
//                                });
//                    }
//                });


    }

    void getStudiedSets() {
        studySetHashMap.clear();
        if(studySetAdapter != null){
            studySets.clear();
            studySetAdapter.notifyDataSetChanged();
        }



        Log.d("hash", "getAllData: " + studySetHashMap.size());
        refLearn.whereEqualTo("userEmail", currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            return;
                        }
                        for (DocumentSnapshot d : task.getResult()
                        ) {
                            Learn learn = d.toObject(Learn.class);
                            if (!studySetHashMap.containsKey(learn.getStudySetID())) {
                                studySetRef.document(learn.getStudySetID())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (!task.getResult().exists()) {
                                                    return;
                                                }
                                                StudySet studySet = task.getResult().toObject(StudySet.class);
                                                studySet.setStudySetID(task.getResult().getId());
                                                studySetRef.document(learn.getStudySetID())
                                                        .collection("terms")
                                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                                ArrayList<Term> terms = new ArrayList<>();
                                                                Log.d("term", "onComplete: " + task1.getResult().getDocuments().toString());
                                                                for (DocumentSnapshot d : task1.getResult()
                                                                ) {
                                                                    Term term = d.toObject(Term.class);
                                                                    terms.add(term);
                                                                }
                                                                studySet.setTerms(terms);
                                                                studySetHashMap.put(learn.getStudySetID(), studySet);
                                                                onDataLoaded();
                                                            }
                                                        });

                                            }
                                        });
                            }
                        }

                    }

                });


    }

    void getCreatedStudySets() {
        studySetHashMap.clear();
        if(studySetAdapter != null){
            studySets.clear();
            studySetAdapter.notifyDataSetChanged();
        }
        SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        studySetRef.whereEqualTo("user", currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            pDialog.cancel();
                        }
                        for (DocumentSnapshot d : task.getResult()
                        ) {
                            StudySet studySet = d.toObject(StudySet.class);
                            studySet.setStudySetID(d.getId());
                            studySetRef.document(studySet.getStudySetID())
                                    .collection("terms")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.getResult().isEmpty()) {
                                                return;
                                            }
                                            ArrayList<Term> terms = new ArrayList<>();
                                            for (DocumentSnapshot d : task.getResult()
                                            ) {
                                                Term term = d.toObject(Term.class);
                                                terms.add(term);
                                            }
                                            studySet.setTerms(terms);
                                            studySetHashMap.put(studySet.getStudySetID(), studySet);
                                            onDataLoaded();
                                            pDialog.cancel();

                                        }
                                    });
                        }
                    }
                });
    }

    void bindingAction() {

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                studySetHashMap.clear();
                if(studySetAdapter != null){
                    studySets.clear();
                    studySetAdapter.notifyDataSetChanged();
                }

                studySetRef.whereEqualTo("user", currentUser.getEmail())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().isEmpty()) {
                                    return;
                                }
                                for (DocumentSnapshot d : task.getResult()
                                ) {
                                    StudySet studySet = d.toObject(StudySet.class);
                                    studySet.setStudySetID(d.getId());
                                    if (studySet.getStudySetName().contains(s.toString())) {
                                        database.collection("studySets")
                                                .document(d.getId())
                                                .collection("terms")
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.getResult().isEmpty()) {
                                                            return;
                                                        }
                                                        ArrayList<Term> terms = new ArrayList<>();
                                                        for (DocumentSnapshot d : task.getResult()
                                                        ) {
                                                            Term term = d.toObject(Term.class);
                                                            terms.add(term);
                                                        }
                                                        studySet.setTerms(terms);
                                                        studySetHashMap.put(studySet.getStudySetID(), studySet);

                                                    }
                                                });
                                    }
                                }
                            }
                        });
                refLearn.whereEqualTo("userEmail", currentUser.getEmail())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().isEmpty()) {
                                    return;
                                }
                                for (DocumentSnapshot d : task.getResult()
                                ) {
                                    Learn learn = d.toObject(Learn.class);
                                    if (!studySetHashMap.containsKey(learn.getStudySetID())) {
                                        studySetRef.document(learn.getStudySetID())
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (!task.getResult().exists()) {
                                                            return;
                                                        }
                                                        StudySet studySet = task.getResult().toObject(StudySet.class);
                                                        if (!studySet.getStudySetName().contains(s.toString())) {
                                                            return;
                                                        }
                                                        studySet.setStudySetID(task.getResult().getId());
                                                        studySetRef.document(studySet.getStudySetID())
                                                                .collection("terms")
                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                                        ArrayList<Term> terms = new ArrayList<>();
                                                                        Log.d("term", "onComplete: " + task1.getResult().getDocuments().toString());
                                                                        for (DocumentSnapshot d : task1.getResult()
                                                                        ) {
                                                                            Term term = d.toObject(Term.class);
                                                                            terms.add(term);
                                                                        }
                                                                        studySet.setTerms(terms);
                                                                        studySetHashMap.put(studySet.getStudySetID(), studySet);
                                                                        onDataLoaded();
                                                                    }
                                                                });

                                                    }
                                                });
                                    }
                                }

                            }

                        });
//                database.collection("learn")
//                        .whereEqualTo("userEmail", currentUser.getEmail())
//                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                for (DocumentSnapshot d : task.getResult()
//                                ) {
//                                    Learn learn = d.toObject(Learn.class);
//                                    if (!studySetHashMap.containsKey(learn.getStudySetID())) {
//                                        database.collection("studySets")
//                                                .document(learn.getStudySetID())
//                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                        if (!task.getResult().exists()) {
//                                                            return;
//                                                        }
//                                                        StudySet studySet = task.getResult().toObject(StudySet.class);
//                                                        if (studySet.getStudySetName().contains(s.toString())) {
//                                                            studySetHashMap.put(learn.getStudySetID(), task.getResult().toObject(StudySet.class));
//                                                        }
//                                                    }
//                                                });
//                                    }
//                                }
//                                onDataLoaded();
//                            }
//                        });

            }
        });

        btnSearchAll.setOnClickListener(v -> {
            etSearch.setVisibility(View.VISIBLE);
            btnSearchAll.setBackgroundColor(Color.parseColor("#38B6FF"));
            btnSearchAll.setTextColor(Color.parseColor("#ffffff"));
            btnSearchCreated.setBackgroundColor(Color.parseColor("#ffffff"));
            btnSearchCreated.setTextColor(Color.parseColor("#000000"));
            btnSearchStudied.setBackgroundColor(Color.parseColor("#ffffff"));
            btnSearchStudied.setTextColor(Color.parseColor("#000000"));
            getAllData();
        });

        btnSearchStudied.setOnClickListener(v -> {
            etSearch.setVisibility(View.INVISIBLE);
            btnSearchStudied.setBackgroundColor(Color.parseColor("#38B6FF"));
            btnSearchStudied.setTextColor(Color.parseColor("#ffffff"));
            btnSearchCreated.setBackgroundColor(Color.parseColor("#ffffff"));
            btnSearchCreated.setTextColor(Color.parseColor("#000000"));
            btnSearchAll.setBackgroundColor(Color.parseColor("#ffffff"));
            btnSearchAll.setTextColor(Color.parseColor("#000000"));
            getStudiedSets();

        });

        btnSearchCreated.setOnClickListener(v -> {
            etSearch.setVisibility(View.INVISIBLE);
            btnSearchCreated.setBackgroundColor(Color.parseColor("#38B6FF"));
            btnSearchCreated.setTextColor(Color.parseColor("#ffffff"));
            btnSearchStudied.setBackgroundColor(Color.parseColor("#ffffff"));
            btnSearchStudied.setTextColor(Color.parseColor("#000000"));
            btnSearchAll.setBackgroundColor(Color.parseColor("#ffffff"));
            btnSearchAll.setTextColor(Color.parseColor("#000000"));
            getCreatedStudySets();

        });

        btnBack.setOnClickListener(view -> {
            ((MainActivity) getActivity()).replaceFragment(new HomeFragment());
        });

        btnAddSet.setOnClickListener(view -> {
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