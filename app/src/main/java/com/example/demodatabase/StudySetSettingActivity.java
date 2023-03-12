package com.example.demodatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.demodatabase.model.Term;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StudySetSettingActivity extends AppCompatActivity {
    CardView cardCancel, cardDeleteSet, cardEditSet;
    String studySetID;
    FirebaseFirestore database;
    ArrayList<Term> terms;

    void initUI() {
        getSupportActionBar().hide();
        cardCancel = findViewById(R.id.card_cancel);
        cardDeleteSet = findViewById(R.id.card_deleteSet);
        cardEditSet = findViewById(R.id.card_editSet);
    }

    void initData() {
        database = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
        terms = (ArrayList<Term>) args.getSerializable("terms");
        studySetID = getIntent().getStringExtra("studySetID");

    }

    void bindingAction() {
        cardCancel.setOnClickListener(view -> {
            onBackPressed();
        });

        cardDeleteSet.setOnClickListener(view -> {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("Won't be able to recover this set!")
                    .setConfirmText("Yes,delete it!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            database.collection("studySets")
                                    .document(studySetID)
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sDialog.dismissWithAnimation();
                                                SweetAlertDialog sweetAlertDialog2 = new SweetAlertDialog(StudySetSettingActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                        .setContentText("Delete successfully");
                                                sweetAlertDialog2.show();
                                                sweetAlertDialog2.getButton(SweetAlertDialog.BUTTON_CONFIRM).setVisibility(View.GONE);
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sweetAlertDialog2.cancel();
                                                        Intent intent = new Intent(StudySetSettingActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }, 1000);


                                            }
                                        }
                                    });

                        }
                    })
                    .show();
        });


        cardEditSet.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditStudySetActivity.class);
            intent.putExtra("studySetID", studySetID);
            Bundle args = new Bundle();
            args.putSerializable("terms", (Serializable) terms);
            intent.putExtra("bundle", args);
            startActivity(intent);
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_set_setting);
        initUI();
        initData();
        bindingAction();
    }
}