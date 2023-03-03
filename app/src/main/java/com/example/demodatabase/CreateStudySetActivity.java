package com.example.demodatabase;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class CreateStudySetActivity extends AppCompatActivity {
    ScrollView scrollView;
    RelativeLayout header;
    ImageView backView;
    EditText etTitle, etDescription;
    TextView tvShowDes, tvDescription;


    private float mTouchPosition;
    private float mReleasePosition;

    void init() {
        scrollView = findViewById(R.id.scrollView);
        header = findViewById(R.id.rl_header);
        backView = findViewById(R.id.imv_back);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        tvShowDes = findViewById(R.id.tv_showDes);
        tvDescription = findViewById(R.id.tv_description);
        getSupportActionBar().hide();
    }

    void initData() {
        // To input a description to a set is optional
        etDescription.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
    }

    void bindingAction() {

        // when scroll down/up the header disappears/appears ( don't how to make the drawer effect yet)
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTouchPosition = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mReleasePosition = event.getY();

                    if (mTouchPosition - mReleasePosition > 0) {
                        Log.d("scroll", "up");
                        header.setVisibility(View.GONE);

                    } else {
                        Log.d("scroll", "down");
                        header.setVisibility(View.VISIBLE);
                    }
                }
                return CreateStudySetActivity.super.onTouchEvent(event);
            }
        });

        // Set back icon to back
        backView.setOnClickListener(view -> {
            onBackPressed();
        });

        // create a option for input the description
        tvShowDes.setOnClickListener(view -> {
            if(etDescription.getVisibility() == View.GONE && tvDescription.getVisibility() == View.GONE){
                etDescription.setVisibility(View.VISIBLE);
                tvDescription.setVisibility(View.VISIBLE);
                tvShowDes.setText("- Description");
            }else {
                etDescription.setVisibility(View.GONE);
                tvDescription.setVisibility(View.GONE);
                tvShowDes.setText("+ Description");
            }

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