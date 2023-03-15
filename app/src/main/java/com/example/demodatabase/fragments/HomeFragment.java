package com.example.demodatabase.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.MainActivity;
import com.example.demodatabase.FolderDetailActivity;
import com.example.demodatabase.R;
import com.example.demodatabase.StudySetDetailActivity;
import com.example.demodatabase.adapter.FolderAdapter;
import com.example.demodatabase.clickinterface.FolderItemClickListener;
import com.example.demodatabase.clickinterface.OnItemClickedListener;
import com.example.demodatabase.adapter.StudySetAdapter;
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


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView recyclerViewFolder;
    StudySetAdapter studySetAdapter;
    FolderAdapter folderAdapter;
    FirebaseUser currentUser;
    FirebaseFirestore database;
    ArrayList<StudySet> studySets = new ArrayList<>();
    ArrayList<Folder> folders = new ArrayList<>();
    ProgressDialog progressDialog;
    TextView viewAll;

    public HomeFragment() {
        // Required empty public constructor
    }

    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.rv_studySetsHome);
        recyclerViewFolder = view.findViewById(R.id.rv_folderHome);
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // get current user (session)
        progressDialog = new ProgressDialog(getContext());
        viewAll = view.findViewById(R.id.tv_viewAllStudySet);
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
                                            onDataLoaded();
                                            progressDialog.dismiss();


                                        }
                                    });

                            Log.d("INFO", d.getData().toString());
                        }

                    }
                });
    }



    void bindingAction(){
        viewAll.setOnClickListener(view -> {
            ((MainActivity) getActivity()).replaceFragment(new SearchOwnStudySetFragment());
        });
    }

    void onDataLoaded() {
        studySetAdapter = new StudySetAdapter(studySets, getActivity(), new OnItemClickedListener() {
            @Override
            public void onItemClick(StudySet item, int pos) {
                Intent intent = new Intent(getContext(), StudySetDetailActivity.class);
                intent.putExtra("studySetID", item.getStudySetID());
                startActivity(intent);
                ;
            }

        });

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(studySetAdapter);
    }

    private void initDataFolder() {
        String email = currentUser.getEmail();
        CollectionReference collectionReference = database.collection("folders");

        progressDialog.show();
        collectionReference.whereEqualTo("user", email)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot d : task.getResult()
                        ) {
                            Folder folder = d.toObject(Folder.class);
                            folder.setFolderID(d.getId());
                            System.out.println("id o luc add " + folder.getFolderID());
                            folders.add(folder);
                            database.collection("folders")
                                    .document(d.getId())
                                    .collection("studySets")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            ArrayList<StudySet> studySets = new ArrayList<>();
                                            for (DocumentSnapshot d : task.getResult()
                                            ) {
                                                StudySet set = d.toObject(StudySet.class);
                                                studySets.add(set);
                                            }
                                            folder.setStudysets(studySets);

                                        }
                                    });

                            Log.d("INFO", d.getData().toString());
                        }
                        progressDialog.dismiss();
                        onDataLoadedFolder();

                    }
                });
    }


    void onDataLoadedFolder() {
        System.out.println("on data loaded");
        folderAdapter = new FolderAdapter(folders, getActivity(), new FolderItemClickListener() {

            @Override
            public void onItemFolderClick(Folder item, int pos) {
                Intent intent = new Intent(getContext(), FolderDetailActivity.class);
                intent.putExtra("folderID", item.getFolderID());
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFolder.setLayoutManager(layoutManager);
        recyclerViewFolder.setAdapter(folderAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home2, container, false);
        initUI(view);
        initData();
        initDataFolder();
        bindingAction();
        return view;

    }
}