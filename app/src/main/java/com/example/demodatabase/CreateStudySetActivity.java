package com.example.demodatabase;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.adapter.CreateTermAdapter;
import com.example.demodatabase.clickinterface.TermItemClickListener;
import com.example.demodatabase.model.StudySet;
import com.example.demodatabase.model.Term;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CreateStudySetActivity extends AppCompatActivity {
    ScrollView scrollView;
    RecyclerView rc_createStudyTerms;
    RelativeLayout header;
    ImageView backView, addingTerm, checkFinish;
    EditText etTitle, etDescription;
    TextView tvShowDes, tvDescription;
    CreateTermAdapter createTermAdapter;
    FirebaseFirestore database;
    FirebaseUser currentUser;
    private ArrayList<Term> terms = new ArrayList<>();
    SweetAlertDialog sweetAlertDialog;

    private boolean isFilled;

    private float mTouchPosition;
    private float mReleasePosition;

    void init() {
        getSupportActionBar().hide();
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
        createTermAdapter = new CreateTermAdapter(terms, this, new TermItemClickListener() {
            @Override
            public void onItemClick(Term item, int pos) {

            }
        });
    }

    void initData() {
        // To input a description to a set is optional
        etDescription.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        Term termRequired = new Term("", "");
        Term termRequired2 = new Term("", "");
        // The set must have 2 initial terms in the beginning
        terms.add(termRequired);
        terms.add(termRequired2);

        createTermAdapter.notifyDataSetChanged();
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
            createTermAdapter.notifyDataSetChanged();
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
                        .setContentText("Add a title to finish creating your set")
                        .show();
                return;
            }

            StudySet studySet = new StudySet();
            studySet.setStudySetName(etTitle.getText().toString());
            studySet.setDate(new Date());
            studySet.setDescription(etDescription.getText().toString());
            studySet.setUser(currentUser.getEmail());
            studySet.setDisplayName(currentUser.getDisplayName());
            studySet.setImageUri(String.valueOf(currentUser.getPhotoUrl()));

            database.collection("studySets").add(studySet).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    for (Term t : terms
                    ) {
                        task.getResult().collection("terms").add(t);
                    }
                    new SweetAlertDialog(CreateStudySetActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Good job!")
                            .setContentText("Created successfully")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(CreateStudySetActivity.this, StudySetDetailActivity.class);
                                    intent.putExtra("studySetID", task.getResult().getId());
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