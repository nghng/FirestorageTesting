package com.example.demodatabase.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.adapter.OnItemClickedListener;
import com.example.demodatabase.adapter.StudySetAdapter;
import com.example.demodatabase.model.StudySet;
import com.example.demodatabase.model.Term;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    StudySetAdapter studySetAdapter;
    FirebaseUser currentUser;
    FirebaseFirestore database;
    ArrayList<StudySet> studySets = new ArrayList<>();
    ProgressDialog progressDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.rv_studySetsHome);
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // get current user (session)
        progressDialog = new ProgressDialog(getContext());
    }

    private void initData() {
        String email = currentUser.getEmail();
        CollectionReference collectionReference = database.collection("studySets");

        progressDialog.show();
        collectionReference.whereEqualTo("user", email)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot d : task.getResult()
                        ) {
                            StudySet studySet = d.toObject(StudySet.class);
                            studySets.add(studySet);
                            database.collection("studySets")
                                            .document(d.getId())
                                                    .collection("terms")
                                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            ArrayList<Term> terms = new ArrayList<>();
                                            for (DocumentSnapshot d: task.getResult()
                                                 ) {
                                                Term term = d.toObject(Term.class);
                                                terms.add(term);
                                            }
                                            studySet.setTerms(terms);

                                        }
                                    });

                            Log.d("INFO", d.getData().toString());
                        }
                        progressDialog.dismiss();
                        onDataLoaded();

                    }
                });
    }


    void onDataLoaded() {
        studySetAdapter = new StudySetAdapter(studySets, getActivity(), new OnItemClickedListener() {
            @Override
            public void onItemClick(Object item, int pos) {

            }
        });

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        System.out.println("home fragment");
//        studySetAdapter.getItemViewType(0);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(studySetAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home2, container, false);
        initUI(view);
        initData();

        return view;

    }
}