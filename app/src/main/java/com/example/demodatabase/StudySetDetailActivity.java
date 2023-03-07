package com.example.demodatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StudySetDetailActivity extends AppCompatActivity {


    void initUI(){
        getSupportActionBar().hide();
    }

    void initData(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String studySetID = extras.getString("studySetID");
        }
    }

    void bindingAction(){

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