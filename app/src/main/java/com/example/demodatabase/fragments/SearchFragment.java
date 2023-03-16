package com.example.demodatabase.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

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
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class SearchFragment extends Fragment {
    RecyclerView recyclerView;
    StudySetVerticalAdapter studySetAdapter;
    FirebaseUser currentUser;
    FirebaseFirestore database;
    ArrayList<StudySet> studySets = new ArrayList<>();
    ArrayList<StudySet> existedStudySets = new ArrayList<>();
    ProgressDialog progressDialog;
    SearchView searchView;
    Button btn_all, btn_set, btn_user;
    String currentSearch = "";
    String filter = "all";
    int white, blue, black;
    String selectType="";
    String folderID;
    Folder currentFolder;
    CollectionReference folderRef;

    public SearchFragment(String selectType) {
        this.selectType = selectType;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    void initUI(View view) {
        btn_all = view.findViewById(R.id.btn_allresult);
        btn_set = view.findViewById(R.id.btn_set);
        btn_user = view.findViewById(R.id.btn_user);
        recyclerView = view.findViewById(R.id.rv_search);
        searchView = view.findViewById(R.id.sv_searchbar);
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // get current user (session)
        progressDialog = new ProgressDialog(getContext());

    }

    void lookSelected(Button clickedButton) {
        clickedButton.setBackgroundColor(blue);
        clickedButton.setTextColor(white);
    }

    void lookUnselected(Button clickedButton) {
        clickedButton.setBackgroundColor(white);
        clickedButton.setTextColor(black);
    }

    void unselectAll() {
        lookUnselected(btn_all);
        lookUnselected(btn_set);
        lookUnselected(btn_user);
    }

    void initData() {
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null){
            folderID = extras.getString("folderID");

        }
        folderRef = database.collection("folders");

        white = ContextCompat.getColor(getContext(), R.color.white);
        blue = ContextCompat.getColor(getContext(), R.color.primary_1);
        black = ContextCompat.getColor(getContext(), R.color.black);

        lookSelected(btn_all);

        CollectionReference collectionReference = database.collection("studySets");
        progressDialog.show();
        if(folderID != null){
            collectionReference
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

            if (selectType.equalsIgnoreCase("multipleSelectedItems")) {

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
                                                for (StudySet existedSet: existedStudySets){
                                                    for (int i=0; i<studySets.size();i++){
                                                        if(studySets.get(i).getStudySetID().equals(existedSet.getStudySetID())){
                                                            studySets.remove(i);
                                                            i--;
                                                        }
                                                    }
                                                }
                                                if(existedStudySets.size()!=0){
                                                    ListIterator<StudySet> iter = studySets.listIterator();
                                                    System.out.println("khac 0 nhe");
                                                    for (StudySet existedSet: existedStudySets){
                                                        while(iter.hasNext()){
                                                            if(existedSet.getStudySetID().equals(iter.next().getStudySetID())){
                                                                iter.remove();
                                                            }
                                                        }
                                                    }
                                                    System.out.println("studeys setttttttttttt");
                                                    for(StudySet s: studySets){
                                                        System.out.println(s.getStudySetName()+" |id : "+s.getStudySetID());
                                                    };
                                                    System.out.println("existed setttt");
                                                    for(StudySet s: existedStudySets){
                                                        System.out.println(s.getStudySetName()+" |id : "+s.getStudySetID());
                                                    };

                                                }
                                                onDataLoaded();
                                            }
                                        });
                            }
                        });
            }

        }else{
            collectionReference
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                                ArrayList<Term> terms = new ArrayList<>();
                                                for (DocumentSnapshot d : task.getResult()
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
    }

    void onDataLoaded() {
        studySetAdapter = new StudySetVerticalAdapter(studySets, getActivity(), new OnItemClickedListener() {
            @Override

            public void onItemClick(StudySet item, int pos) {
                if (selectType.equalsIgnoreCase("multipleSelectedItems")) {
                    item.setSelected(!item.isSelected());
                    if (item.isSelected())
                        AddSetToFolderActivity.selectedStudySets.add(item);
                    else
                        AddSetToFolderActivity.selectedStudySets.remove(item);
                } else {
                    Intent intent = new Intent(getContext(), StudySetDetailActivity.class);
                    intent.putExtra("studySetID", item.getStudySetID());
                    startActivity(intent);
                    ;
                }

            }

        });

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
//        System.out.println("study set fragment");
//       studySetAdapter.getItemViewType(1);
        recyclerView.setAdapter(studySetAdapter);

    }

    void event() {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                studySetAdapter.getFilter(filter).filter(query);
                currentSearch = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                studySetAdapter.getFilter(filter).filter(newText);
                currentSearch = newText;
                return false;
            }

        });

        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("button click set");
                unselectAll();
                lookSelected(btn_set);
                filter = "set";
                studySetAdapter.getResult(currentSearch, filter);
            }

        });

        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("button click user");
                unselectAll();
                lookSelected(btn_user);
                filter = "user";
                studySetAdapter.getResult(currentSearch, filter);
            }

        });

        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("button click all");
                unselectAll();
                lookSelected(btn_all);
                filter = "all";
                studySetAdapter.getResult(currentSearch, filter);
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initUI(view);
        initData();
        event();
        return view;

    }
}