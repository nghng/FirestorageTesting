package com.example.demodatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.demodatabase.adapter.CreateTermAdapter;
import com.example.demodatabase.adapter.EditTermAdapter;
import com.example.demodatabase.clickinterface.EditTermClickedListener;
import com.example.demodatabase.clickinterface.TermItemClickListener;
import com.example.demodatabase.model.Question;
import com.example.demodatabase.model.StudySet;
import com.example.demodatabase.model.Term;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditStudySetActivity extends AppCompatActivity {

    ScrollView scrollView;
    RecyclerView rc_createStudyTerms;
    RelativeLayout header;
    ImageView backView, addingTerm, checkFinish;
    EditText etTitle, etDescription;
    TextView tvShowDes, tvDescription, tvCreateStudySetText;
    EditTermAdapter createTermAdapter;
    FirebaseFirestore database;
    FirebaseUser currentUser;
    private ArrayList<Term> terms = new ArrayList<>();
    private String studySetID;
    SweetAlertDialog sweetAlertDialog;
    StudySet currentStudySet;
    List<Term> deleteTerm = new ArrayList<>();
    List<Term> editAddingTerm = new ArrayList<>();


    private boolean isFilled;

    private float mTouchPosition;
    private float mReleasePosition;

    void init() {
        getSupportActionBar().hide();
        tvCreateStudySetText = findViewById(R.id.tv_createStudySetText);
        scrollView = findViewById(R.id.scrollView);
        rc_createStudyTerms = findViewById(R.id.rc_createStudyTerms);
        header = findViewById(R.id.rl_header);
        backView = findViewById(R.id.imv_back);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        tvShowDes = findViewById(R.id.tv_showDes);
        tvDescription = findViewById(R.id.tv_description);
        addingTerm = findViewById(R.id.imv_addingTerm);
        checkFinish = findViewById(R.id.img_checkFinish);
        database = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sweetAlertDialog = new SweetAlertDialog(this);

    }

    void onDataLoaded() {
        if (currentStudySet.getDescription() != null) {
            etDescription.setText(currentStudySet.getDescription());
            tvShowDes.performClick();
        }
        etTitle.setText(currentStudySet.getStudySetName());
    }

    void initData() {
        // To input a description to a set is optional
        etDescription.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        tvCreateStudySetText.setText("Edit Study Set");
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
        terms = (ArrayList<Term>) args.getSerializable("terms");
        studySetID = getIntent().getStringExtra("studySetID");
        database.collection("studySets")
                .document(studySetID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        currentStudySet = task.getResult().toObject(StudySet.class);
                        Log.d("studyset", currentStudySet.toString());
                        onDataLoaded();
                    }
                });

        createTermAdapter = new EditTermAdapter(terms, this, new EditTermClickedListener() {
            @Override
            public void onDeleteClick(Term item, int pos) {
                deleteTerm.add(item);
            }

            @Override
            public void onAddingClick(Term item, int pos) {
                editAddingTerm.add(item);
            }
        });

        rc_createStudyTerms.setLayoutManager(new LinearLayoutManager(this));
        rc_createStudyTerms.setAdapter(createTermAdapter);

    }


    void bindingAction() {

        // when scroll down/up the header disappears/appears ( don't how to make the drawer effect yet)
//        rc_createStudyTerms.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mTouchPosition = event.getY();
//                }
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mReleasePosition = event.getY();
//
//                    if (mTouchPosition - mReleasePosition > 0) {
//                        Log.d("scroll", "up");
//                        header.setVisibility(View.GONE);
//
//                    } else {
//                        Log.d("scroll", "down");
//                        header.setVisibility(View.VISIBLE);
//                    }
//                }
//                return CreateStudySetActivity.super.onTouchEvent(event);
//            }
//        });

        // Set back icon to back
        backView.setOnClickListener(view -> {
            onBackPressed();
        });

        // create a option for input the description
        tvShowDes.setOnClickListener(view -> {
            if (etDescription.getVisibility() == View.GONE && tvDescription.getVisibility() == View.GONE) {
                etDescription.setVisibility(View.VISIBLE);
                tvDescription.setVisibility(View.VISIBLE);
                tvShowDes.setText("- Description");
            } else {
                etDescription.setVisibility(View.GONE);
                tvDescription.setVisibility(View.GONE);
                tvShowDes.setText("+ Description");
            }

        });
        // Adding a term when click the add button
        addingTerm.setOnClickListener(view -> {
            Term addingTerm = new Term("", "");
            terms.add(addingTerm);
            editAddingTerm.add(addingTerm);
            createTermAdapter.notifyItemInserted(terms.size() - 1);
        });


        // begin the adding process
        checkFinish.setOnClickListener(view -> {
            isFilled = true;
            if (terms.size() < 2) {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Your set must contains at least 2 terms")
                        .show();
                return;
            }

            // Checking whether the all the terms has been filled
            for (Term t : terms
            ) {
                if (t.getDefinition().trim().equals("") || t.getTerm().trim().equals("")) {
                    isFilled = false;
                }
            }
            // checking if all the terms has been filled if not show a popup dialog
            if (!isFilled) {
                if (terms.size() == 2) {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("You must fill in two terms and definitions to publish your set")
                            .show();
                } else {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("You must fill all your terms and definitions to publish your set")
                            .show();
                }
                return;
            }

            // checking if the title is not filled if not show a dialog
            if (etTitle.getText().toString().trim().equals("")) {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Add a title to finish editing your set")
                        .show();
                return;
            }
            DocumentReference studySetRef = database.collection("studySets")
                    .document(studySetID);
            studySetRef.update("description", etDescription.getText().toString(),
                            "studySetName", etTitle.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new SweetAlertDialog(EditStudySetActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Change successfully")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            for (Term t: terms
                                            ) {
                                                if(editAddingTerm.contains(t)){
                                                    continue;
                                                }
                                                studySetRef.collection("terms")
                                                        .document(t.getTermID())
                                                        .update("definition", t.getDefinition(),
                                                                "term", t.getTerm());


                                            }

                                            CollectionReference learnRef = database.collection("learn");
                                            Query query = learnRef.whereEqualTo("studySetID", studySetID);

                                            for (Term t: deleteTerm
                                                 ) {
                                                if(editAddingTerm.contains(t)){
                                                    continue;
                                                }
                                                studySetRef.collection("terms")
                                                        .document(t.getTermID())
                                                        .delete();
                                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        for (QueryDocumentSnapshot q: task.getResult()
                                                             ) {
                                                            CollectionReference questionRef = q.getReference().collection("questions");
                                                            Query query1 = questionRef.whereEqualTo("termID", t.getTermID());
                                                            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    for (QueryDocumentSnapshot q: task.getResult()
                                                                         ) {
                                                                        q.getReference().delete();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }

                                            for (Term term : editAddingTerm){
                                                if(deleteTerm.contains(term)){
                                                    continue;
                                                }
                                                studySetRef.collection("terms").add(term).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        Question question = new Question();
                                                        question.setTerm(term);
                                                        question.setTermID(task.getResult().getId());
                                                        question.setAnswerRight(false);
                                                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                for (QueryDocumentSnapshot q: task.getResult()
                                                                     ) {
                                                                          q.getReference().collection("questions")
                                                                                  .add(question);
                                                                }
                                                            }
                                                        });
                                                    }
                                                });


                                            }



                                            Intent intent = new Intent(EditStudySetActivity.this, StudySetDetailActivity.class);
                                            intent.putExtra("studySetID", studySetID);

                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        }
                    });



        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_study_set);
        init();
        initData();
        bindingAction();


    }
}