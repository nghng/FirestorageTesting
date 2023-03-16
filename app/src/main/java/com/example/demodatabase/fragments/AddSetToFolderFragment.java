package com.example.demodatabase.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.AddSetToFolderActivity;
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
import java.util.ListIterator;


public class AddSetToFolderFragment extends Fragment {
    RecyclerView recyclerView;
    StudySetVerticalAdapter studySetAdapter;
    FirebaseUser currentUser;
    FirebaseFirestore database;
    ArrayList<StudySet> studySets = new ArrayList<>();
    ArrayList<StudySet> existedStudySets = new ArrayList<>();
    ProgressDialog progressDialog;
    String folderID;
    Folder currentFolder;
    CollectionReference folderRef;
    ImageView imv_noneset, imv_nonefolder;


    public AddSetToFolderFragment() {
    }

    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.rv_studySetsProfile);
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // get current user (session)
        progressDialog = new ProgressDialog(getContext());
        imv_noneset = view.findViewById(R.id.noneStudySet);
        imv_nonefolder=view.findViewById(R.id.nonefolder);
        imv_nonefolder.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        Bundle extras = getActivity().getIntent().getExtras();
        folderID = extras.getString("folderID");
        folderRef = database.collection("folders");
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
                            studySet.setStudySetID(d.getId());
                            studySets.add(studySet);
                            database.collection("studySets")
                                    .document(d.getId())
                                    .collection("terms")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            ArrayList<Term> terms = new ArrayList<>();
                                            for (DocumentSnapshot d : task.getResult()
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


                    }
                });


        folderRef.document(folderID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        currentFolder = task.getResult().toObject(Folder.class);
                        folderRef.document(folderID)
                                .collection("studySets")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot d : task.getResult()
                                        ) {

                                            StudySet set = d.toObject(StudySet.class);
                                            set.setStudySetID(d.getId());
                                            existedStudySets.add(set);
                                        }
                                        if (existedStudySets.size() != 0) {
                                            System.out.println("khac 0 nhe");
                                            for (StudySet existedSet : existedStudySets) {
                                                for (int i = 0; i < studySets.size(); i++) {
                                                    if (studySets.get(i).getStudySetID().equals(existedSet.getStudySetID())) {
                                                        studySets.remove(i);
                                                        i--;
                                                    }
                                                }
                                            }
                                            System.out.println("studeys setttttttttttt");
                                            for (StudySet s : studySets) {
                                                System.out.println(s.getStudySetName() + " |id : " + s.getStudySetID());
                                            }
                                            ;
                                            System.out.println("existed setttt");
                                            for (StudySet s : existedStudySets) {
                                                System.out.println(s.getStudySetName() + " |id : " + s.getStudySetID());
                                            }
                                            ;

                                        }
                                        onDataLoaded();
                                    }
                                });
                    }
                });


    }


    void onDataLoaded() {
        if (studySets.size() == 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            imv_noneset.setVisibility(View.VISIBLE);

        } else {
            recyclerView.setVisibility(View.VISIBLE);
            imv_noneset.setVisibility(View.INVISIBLE);
            studySetAdapter = new StudySetVerticalAdapter(studySets, getActivity(), new OnItemClickedListener() {
                @Override
                public void onItemClick(StudySet item, int pos) {
                    item.setSelected(!item.isSelected());
                    System.out.println(item.isSelected());
                    if (item.isSelected()) {
                        AddSetToFolderActivity.selectedStudySets.add(item);
                    } else
                        AddSetToFolderActivity.selectedStudySets.remove(item);
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
