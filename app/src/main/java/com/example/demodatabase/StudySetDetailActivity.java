package com.example.demodatabase;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.demodatabase.adapter.FlipTermAdapter;
import com.example.demodatabase.model.StudySet;
import com.example.demodatabase.model.Term;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class StudySetDetailActivity extends AppCompatActivity {
    ImageView imvBack, imvSetting;
    ViewPager rcFlipTerm;
    StudySet currentStudySet;
    FlipTermAdapter flipTermAdapter;
    FirebaseFirestore database;
    String studySetID;
    CircleIndicator circleIndicator;

    void initUI() {
        getSupportActionBar().hide();
        imvBack = findViewById(R.id.imv_back);
        imvSetting = findViewById(R.id.img_setting);
        rcFlipTerm = findViewById(R.id.rv_flipTerms);
        circleIndicator = findViewById(R.id.ci_circleIndicator);
    }

    void onDataLoaded(){
        flipTermAdapter = new FlipTermAdapter(currentStudySet.getTerms(), this);
        rcFlipTerm.setAdapter(flipTermAdapter);
        circleIndicator.setViewPager(rcFlipTerm);
        flipTermAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
    }

    void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            studySetID = extras.getString("studySetID");
            database = FirebaseFirestore.getInstance();
            CollectionReference studySetsRef = database.collection("studySets");

            studySetsRef.document(studySetID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            currentStudySet = task.getResult().toObject(StudySet.class);
                            studySetsRef.document(studySetID)
                                    .collection("terms")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            ArrayList<Term> terms = new ArrayList<>();
                                            for (DocumentSnapshot d: task.getResult()
                                            ) {
                                                Term term = d.toObject(Term.class);
                                                terms.add(term);
                                                term.setTermID(d.getId());
                                            }
                                            currentStudySet.setTerms(terms);
                                            onDataLoaded();
                                        }

                                    });

                        }
                    });
        }


    }

    void bindingAction() {
        imvBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_set_detail);
        initUI();
        initData();
        bindingAction();
    }
}