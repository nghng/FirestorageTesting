package com.example.demodatabase.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.demodatabase.R;
import com.example.demodatabase.StudySetDetailActivity;
import com.example.demodatabase.adapter.StudySetAdapter;
import com.example.demodatabase.adapter.StudySetVerticalAdapter;
import com.example.demodatabase.clickinterface.OnItemClickedListener;
import com.example.demodatabase.model.Folder;
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

import java.util.ArrayList;
import java.util.Arrays;


public class ProfileSetFragment extends Fragment {
    RecyclerView recyclerView;
    StudySetVerticalAdapter studySetAdapter;
    FirebaseUser currentUser;
    FirebaseFirestore database;
    ArrayList<StudySet> studySets = new ArrayList<>();
    ProgressDialog progressDialog;
    String studySetID;
    ImageView imv_nonefolder;
    ImageView imv_nonestudyset;
    public ProfileSetFragment(){
    }

    private void initUI(View view) {
        imv_nonefolder=view.findViewById(R.id.nonefolder);
        imv_nonestudyset=view.findViewById(R.id.noneStudySet);
        imv_nonefolder.setVisibility(View.INVISIBLE);
        recyclerView = view.findViewById(R.id.rv_studySetsProfile);
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // get current user (session)
        progressDialog = new ProgressDialog(getContext());
    }

    private void initData() {
        String email = currentUser.getEmail();
        CollectionReference collectionReference = database.collection("studySets");

        progressDialog.show();
        collectionReference.whereEqualTo("user",email)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot d : task.getResult()
                        ) {
                            StudySet studySet = d.toObject(StudySet.class);
                            studySet.setStudySetID(studySetID=d.getId());
                            database.collection("studySets")
                                    .document(studySet.getStudySetID())
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
                                            studySets.add(studySet);
                                            onDataLoaded();



                                        }
                                    });

                            Log.d("INFO", d.getData().toString());
                        }
                        progressDialog.dismiss();

                    }
                });
    }



    void onDataLoaded() {
        if(studySets.size()==0){
            recyclerView.setVisibility(View.INVISIBLE);
            imv_nonestudyset.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            imv_nonestudyset.setVisibility(View.INVISIBLE);
            studySetAdapter = new StudySetVerticalAdapter(studySets, getActivity(), new OnItemClickedListener() {
                @Override
                public void onItemClick(StudySet item, int pos) {
                    Intent intent = new Intent(getContext(), StudySetDetailActivity.class);
                    intent.putExtra("studySetID", item.getStudySetID());
                    startActivity(intent);;
                }

            });

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
//        System.out.println("study set fragment");
//       studySetAdapter.getItemViewType(1);
            recyclerView.setAdapter(studySetAdapter);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_set, container, false);
        initUI(view);
        initData();

        return view;

    }


}