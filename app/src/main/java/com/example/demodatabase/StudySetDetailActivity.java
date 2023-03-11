package com.example.demodatabase;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.demodatabase.adapter.FlipTermAdapter;
import com.example.demodatabase.adapter.ItemTermCardAdapter;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class StudySetDetailActivity extends AppCompatActivity {
    CardView originalCard, alphaCard, cardLearn;
    ConstraintLayout wrapper, clTapOut;
    RecyclerView rcCardTerm, optionSort;
    TextView tvStudySetName, tvDisplayName, numberOfTerms;
    ImageView imvBack, imvSetting, imvAccountImage, imvFilerCardTerm, alphaTick, oriTick, setting;
    ViewPager rcFlipTerm;
    StudySet currentStudySet;
    FlipTermAdapter flipTermAdapter;
    FirebaseFirestore database;
    String studySetID;
    CircleIndicator circleIndicator;
    FirebaseUser currentUser;
    ItemTermCardAdapter itemTermCardAdapter;



    void initUI() {
        getSupportActionBar().hide();
        imvBack = findViewById(R.id.imv_back);
        imvSetting = findViewById(R.id.imv_setting);
        rcFlipTerm = findViewById(R.id.rv_flipTerms);
        circleIndicator = findViewById(R.id.ci_circleIndicator);
        tvStudySetName = findViewById(R.id.tv_studySetName);
        tvDisplayName = findViewById(R.id.tv_displayName);
        imvAccountImage = findViewById(R.id.imv_accountImage);
        numberOfTerms = findViewById(R.id.tv_numberOfTerms);
        rcCardTerm = findViewById(R.id.rc_cardTerm);
        imvFilerCardTerm = findViewById(R.id.imv_filerCardTerm);
        wrapper = findViewById(R.id.wrapper);
        clTapOut = findViewById(R.id.cl_tapOut);
        alphaTick = findViewById(R.id.alphaTick);
        oriTick = findViewById(R.id.originalTick);
        originalCard = findViewById(R.id.card_originalOrder);
        alphaCard = findViewById(R.id.card_alphaOrder);
        cardLearn = findViewById(R.id.card_learn);
        // Option sort will be not visible when first load
        wrapper.setVisibility(View.INVISIBLE);

    }

    void onDataLoaded() {
        flipTermAdapter = new FlipTermAdapter(currentStudySet.getTerms(), this);
        rcFlipTerm.setAdapter(flipTermAdapter);
        circleIndicator.setViewPager(rcFlipTerm);
        flipTermAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
        tvStudySetName.setText(currentStudySet.getStudySetName());
        tvDisplayName.setText(currentUser.getDisplayName());
        numberOfTerms.setText(currentStudySet.getTerms().size() + " terms");
        Glide.with(this).load(currentUser.getPhotoUrl()).error(R.drawable.default_user_image)
                .into(imvAccountImage);

        itemTermCardAdapter = new ItemTermCardAdapter(this, currentStudySet.getTerms());
        rcCardTerm.setLayoutManager(new LinearLayoutManager(this));
        rcCardTerm.setAdapter(itemTermCardAdapter);

    }

    void initData() {
        Bundle extras = getIntent().getExtras();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                                            for (DocumentSnapshot d : task.getResult()
                                            ) {
                                                Term term = d.toObject(Term.class);
                                                terms.add(term);
                                                term.setTermID(d.getId());
                                            }
                                            currentStudySet.setTerms(terms);
                                            // load data to UI
                                            onDataLoaded();
                                        }

                                    });

                        }
                    });
        }


    }

    void bindingAction() {
        imvBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        imvFilerCardTerm.setOnClickListener(view -> {
            slideUp(wrapper);
        });

        clTapOut.setOnClickListener(view -> {
            slideDown(wrapper);
        });
        CollectionReference studySetsRef = database.collection("studySets");

        originalCard.setOnClickListener(view -> {
            oriTick.setVisibility(View.VISIBLE);
            alphaTick.setVisibility(View.INVISIBLE);
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
                                            for (DocumentSnapshot d : task.getResult()
                                            ) {
                                                Term term = d.toObject(Term.class);
                                                terms.add(term);
                                                term.setTermID(d.getId());
                                            }
                                            currentStudySet.setTerms(terms);
                                            // load data to UI
                                            onDataLoaded();
                                            slideDown(wrapper);
                                        }
                                    });
                        }
                    });
        });

        alphaCard.setOnClickListener(view -> {
            oriTick.setVisibility(View.INVISIBLE);
            alphaTick.setVisibility(View.VISIBLE);
            studySetsRef.document(studySetID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            currentStudySet = task.getResult().toObject(StudySet.class);
                            studySetsRef.document(studySetID)
                                    .collection("terms")
                                    .orderBy("term")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            ArrayList<Term> terms = new ArrayList<>();
                                            for (DocumentSnapshot d : task.getResult()
                                            ) {
                                                Term term = d.toObject(Term.class);
                                                terms.add(term);
                                                term.setTermID(d.getId());
                                            }
                                            currentStudySet.setTerms(terms);
                                            // load data to UI
                                            onDataLoaded();
                                            slideDown(wrapper);
                                        }
                                    });
                        }
                    });
        });

        cardLearn.setOnClickListener(view -> {
            Intent intent = new Intent(this, LearnStudySetActivity.class);
            intent.putExtra("studySetID", studySetID);
            Bundle args = new Bundle();
            args.putSerializable("terms", (Serializable) currentStudySet.getTerms());
            intent.putExtra("bundle", args);
            startActivity(intent);
        });

        imvSetting.setOnClickListener(view ->{
            Intent intent = new Intent(this, StudySetSettingActivity.class);
            intent.putExtra("studySetID", studySetID);
            Bundle args = new Bundle();
            args.putSerializable("terms", (Serializable) currentStudySet.getTerms());
            intent.putExtra("bundle", args);
            startActivity(intent);
        });

    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        view.setVisibility(View.INVISIBLE);

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(false);
        view.startAnimation(animate);
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