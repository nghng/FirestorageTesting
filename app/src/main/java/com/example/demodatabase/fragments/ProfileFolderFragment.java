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

import com.example.demodatabase.FolderDetailActivity;
import com.example.demodatabase.R;
import com.example.demodatabase.StudySetDetailActivity;
import com.example.demodatabase.adapter.FolderAdapter;
import com.example.demodatabase.adapter.StudySetAdapter;
import com.example.demodatabase.clickinterface.FolderItemClickListener;
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

public class ProfileFolderFragment extends Fragment {
    RecyclerView recyclerView;
    FolderAdapter folderAdapter;
    FirebaseUser currentUser;
    FirebaseFirestore database;
    ArrayList<Folder> folders = new ArrayList<>();
    ProgressDialog progressDialog;
    ImageView imv_nonefolder;
    ImageView imv_nonestudyset;

    public ProfileFolderFragment(){
    }

    private void initUI(View view) {
        imv_nonefolder=view.findViewById(R.id.nonefolder);
        imv_nonestudyset=view.findViewById(R.id.noneStudySet);
        imv_nonestudyset.setVisibility(View.INVISIBLE);
        recyclerView = view.findViewById(R.id.rv_studySetsProfile);
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // get current user (session)
        progressDialog = new ProgressDialog(getContext());
    }

    private void initData() {
        String email = currentUser.getEmail();
        CollectionReference collectionReference = database.collection("folders");

        progressDialog.show();
        collectionReference.whereEqualTo("user",email)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot d : task.getResult()
                        ) {
                            Folder folder = d.toObject(Folder.class);
                            folder.setFolderID(d.getId());
                            System.out.println("id o luc add "+folder.getFolderID());
                            folders.add(folder);
                            database.collection("folders")
                                    .document(d.getId())
                                    .collection("studySets")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            ArrayList<StudySet> studySets = new ArrayList<>();
                                            for (DocumentSnapshot d: task.getResult()
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
                        onDataLoaded();

                    }
                });
    }



    void onDataLoaded() {
        if(folders.size()==0){
            recyclerView.setVisibility(View.INVISIBLE);
            imv_nonefolder.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            imv_nonefolder.setVisibility(View.INVISIBLE);
            folderAdapter = new FolderAdapter(folders, getActivity(), new FolderItemClickListener() {

                @Override
                public void onItemFolderClick(Folder item, int pos) {
                    Intent intent = new Intent(getContext(), FolderDetailActivity.class);
                    intent.putExtra("folderID", item.getFolderID());
                    startActivity(intent);
                }
            });

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(folderAdapter);
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