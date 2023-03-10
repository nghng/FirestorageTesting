package com.example.demodatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.demodatabase.adapter.ItemAnswerAdapter;
import com.example.demodatabase.clickinterface.AnswerItemClickedListener;
import com.example.demodatabase.model.Answer;
import com.example.demodatabase.model.Learn;
import com.example.demodatabase.model.Question;
import com.example.demodatabase.model.Term;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LearnStudySetActivity extends AppCompatActivity {
    ConstraintLayout clQuestion;
    TextView tvTermInQuestion;
    RecyclerView rcAnswer;
    ImageView imvBack, imvSetting;
    FirebaseUser currentUser;
    FirebaseFirestore database;
    String studySetID;
    Intent intent;
    ItemAnswerAdapter itemAnswerAdapter;
    List<Question> questions = new ArrayList<>(); // question root
    List<Question> unAnswerQuestion = new ArrayList<>();
    CollectionReference learnRef, termRef;
    Question currentQuestion;
    List<Answer> currentAnswer = new ArrayList<>();
    List<Answer> answerRoot = new ArrayList<>();
    String learnID;
    int currentIndex;
    int numberOfQuestion;
    boolean isStudied;
    Answer currentRightAnswer;
    Learn currentLearn;


    void initUI() {
        getSupportActionBar().hide();
        imvBack = findViewById(R.id.imv_back);
        imvSetting = findViewById(R.id.img_setting);
        clQuestion = findViewById(R.id.cl_question);
        tvTermInQuestion = findViewById(R.id.tv_termInQuestion);
        rcAnswer = findViewById(R.id.rc_answers);
    }

    void getCurrentIndex() {
        currentIndex = 0;
        for (Question q : unAnswerQuestion
        ) {
            if (q.getAnswerRight()) {
                currentIndex++;
            } else {
                return;
            }
        }
    }

    void loadDataToAnswerRecycleView() {
        Log.d("currentsize", "loadDataToAnswerRecycleView: " + currentAnswer.size());

        itemAnswerAdapter = new ItemAnswerAdapter(this, (ArrayList<Answer>) currentAnswer, new AnswerItemClickedListener() {
            @Override
            public void onRightAnswerClick(Answer item, int pos) {


                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LearnStudySetActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("Nicely done");
                sweetAlertDialog.show();
                sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sweetAlertDialog.cancel();
                        learnRef.document(learnID).collection("questions")
                                .document(currentQuestion.getQuestionID())
                                .update("answerRight", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        currentAnswer.clear();

                                        isStudied(currentLearn);

                                    }
                                });
                    }
                }, 1000);

            }

            @Override
            public void onWrongAnswerClick(Answer item, int pos) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LearnStudySetActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Correct answer is: " + currentRightAnswer.getAnswer()  )
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                currentAnswer.clear();
                                isStudied(currentLearn);
                                sweetAlertDialog.cancel();
                            }
                        });
                sweetAlertDialog.show();


            }
        });
        rcAnswer.setLayoutManager(new LinearLayoutManager(this));
        rcAnswer.setAdapter(itemAnswerAdapter);
    }

    void dataLoaded() {
        getCurrentIndex();
        currentQuestion = unAnswerQuestion.get(currentIndex);
//        Term currentTerm = getTermByID(currentQuestion.getTermID());
        termRef = database.collection("studySets").document(studySetID).collection("terms");
        if (unAnswerQuestion.size() < 4) {
            numberOfQuestion = unAnswerQuestion.size();
        } else {
            numberOfQuestion = 4;
        }
        termRef.document(currentQuestion.getTermID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {




                // Get true answer
                Answer answer = new Answer();
                answer.setRight(true);
                Term term = task.getResult().toObject(Term.class);
                tvTermInQuestion.setText(term.getTerm());
                currentQuestion.setTerm(term);
                answer.setAnswer(term.getDefinition());
                currentAnswer.add(answer);
                currentRightAnswer = answer;
                itemAnswerAdapter.notifyDataSetChanged();
                int numberOfWrongAnswers = 0;

                unAnswerQuestion.remove(currentIndex);
                while (numberOfWrongAnswers < numberOfQuestion - 1) {
                    int index = (int) (Math.random() * unAnswerQuestion.size());
                    String termID = unAnswerQuestion.get(index).getTermID();
                    unAnswerQuestion.remove(index);
                    if (termID.equals(currentQuestion.getTermID())) {
                        continue;
                    }

                    termRef.document(termID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Answer answer1 = new Answer();
                            answer1.setRight(false);
                            Term term1 = task.getResult().toObject(Term.class);
                            answer1.setAnswer(term1.getDefinition());
                            currentAnswer.add(answer1);
                            itemAnswerAdapter.notifyDataSetChanged();
                            Log.d("Wrong", term1.toString());
                        }

                    });
                    numberOfWrongAnswers++;
                    Log.d("currentanswer", currentAnswer.toString());
                }


            }
        });


    }

    void getQuestion(String learnID) {
        learnRef.document(learnID)
                .collection("questions")
                .whereEqualTo("answerRight", false)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot d : task.getResult()
                        ) {
                            Question question = new Question();

                            question = d.toObject(Question.class);
                            question.setQuestionID(d.getId());
                            unAnswerQuestion.add(question);
                        }
                        Log.d("array", unAnswerQuestion.toString());
                        if(unAnswerQuestion.size() == 0){
                            new SweetAlertDialog(LearnStudySetActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("You have completed")
                                    .show();
                            return;
                        }
                        dataLoaded();


                    }
                });

    }

    void initiateLearnDataForTheFirstTime(Learn learn) {
        learnRef.add(learn).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                for (Question q : questions
                ) {
                    task.getResult().collection("questions").add(q);
                }
                learnID = task.getResult().getId();
                getQuestion(task.getResult().getId());


            }
        });
    }

    void isStudied(Learn learn) {
        isStudied = true;
        learnRef.whereEqualTo("studySetID", studySetID)
                .whereEqualTo("userEmail", currentUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            isStudied = false;

                        }
                        if (!isStudied) {
                            initiateLearnDataForTheFirstTime(learn);
                        } else {
                            String id = task.getResult().getDocuments().get(0).getId();
                            learnID = id;
                            getQuestion(id);

                        }
                    }
                });


    }

    void initData() {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();
        learnRef = database.collection("learn");
        // Get question from question detail
        intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
        ArrayList<Term> terms = (ArrayList<Term>) args.getSerializable("terms");
        studySetID = getIntent().getStringExtra("studySetID");
        questions = Question.createQuestions(terms);
        Learn learn = new Learn();
        learn.setStudySetID(studySetID);
        learn.setTerms(terms);
        learn.setUserEmail(currentUser.getEmail());
        currentLearn = learn;
        loadDataToAnswerRecycleView();
        isStudied(learn);


    }

    void bindingAction() {
        imvBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_study_set);
        initUI();
        initData();
        bindingAction();
    }
}