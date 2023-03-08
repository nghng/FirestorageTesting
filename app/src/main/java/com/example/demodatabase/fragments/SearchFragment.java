package com.example.demodatabase.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
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

import com.example.demodatabase.R;
import com.example.demodatabase.adapter.StudySetAdapter;
import com.example.demodatabase.clickinterface.OnItemClickedListener;
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

public class SearchFragment extends Fragment {
    RecyclerView recyclerView;
    StudySetAdapter studySetAdapter;
    FirebaseUser currentUser;
    FirebaseFirestore database;
    ArrayList<StudySet> studySets = new ArrayList<>();
    ProgressDialog progressDialog;
    SearchView searchView;
    Button btn_all, btn_set, btn_user;
    String currentSearch="";

    int white, blue, black;

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

    void unselectAll(){
        lookUnselected(btn_all);
        lookUnselected(btn_set);
        lookUnselected(btn_user);
    }

    void initData() {
        white = ContextCompat.getColor(getContext(), R.color.white);
        blue = ContextCompat.getColor(getContext(), R.color.primary_1);
        black = ContextCompat.getColor(getContext(), R.color.black);

        lookSelected(btn_all);

        CollectionReference collectionReference = database.collection("studySets");
        progressDialog.show();
        collectionReference
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
                        onDataLoaded();

                    }
                });
    }

    void onDataLoaded() {
        studySetAdapter = new StudySetAdapter(studySets, getActivity(), new OnItemClickedListener() {
            @Override
            public void onItemClick(StudySet item, int pos) {

            }
        });

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
//        System.out.println("study set fragment");
//       studySetAdapter.getItemViewType(1);
        recyclerView.setAdapter(studySetAdapter);

    }

    void event(){
        List<String> filter=new ArrayList<>();
        filter.add("user");
        filter.add("set");

        btn_set.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                unselectAll();
                lookSelected(btn_set);
                filter.clear();
                filter.add("set");
                search(filter);
                studySetAdapter.getResult(currentSearch, filter);
            }

        });

        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAll();
                lookSelected(btn_user);
                filter.clear();
                filter.add("user");
                search(filter);
                studySetAdapter.getResult(currentSearch, filter);
            }
        });

        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAll();
                lookSelected(btn_all);
                filter.clear();
                filter.add("set");
                filter.add("user");
                search(filter);
                studySetAdapter.getResult(currentSearch, filter);
            }
        });

    }

    void search(List<String> filter) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearch=query;

                studySetAdapter.getFilter(filter).filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                studySetAdapter.getFilter(filter).filter(newText);
                return false;
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
        search(Arrays.asList("user","set"));
        event();
        return view;

    }
}