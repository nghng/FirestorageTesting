package com.example.demodatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demodatabase.model.Folder;
import com.example.demodatabase.model.StudySet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FolderDetailActivity extends AppCompatActivity {
    RecyclerView rv_studySets;
    CardView noSet;
    FirebaseFirestore database;
    FirebaseUser currentUser;
    private ArrayList<StudySet> studySets = new ArrayList<>();
    SweetAlertDialog sweetAlertDialog;
    TextView tv_numberOfSets, tv_displayName, tv_folderName, tv_folderDescription;
    ImageView img_AccountImage, img_back, img_kebab_menu;
    String folderID;
    Folder currentFolder;
    int numberOfSets;
    Button btn_addset;

    void initUI() {
        img_back = findViewById(R.id.imv_back);
        tv_numberOfSets = findViewById(R.id.tv_numberOfSets);
        tv_displayName = findViewById(R.id.tv_displayName);
        tv_folderName = findViewById(R.id.tv_folderName);
        tv_folderDescription = findViewById(R.id.tv_folderDescription);
        img_AccountImage = findViewById(R.id.imv_accountImage);
        noSet = findViewById(R.id.cv_noset);
        rv_studySets = findViewById(R.id.rv_studySets);
        img_kebab_menu =findViewById(R.id.img_kebab_menu);
        btn_addset=findViewById(R.id.btn_addset);

    }

    void onDataLoaded() {

        tv_numberOfSets.setText(numberOfSets + (numberOfSets <= 1 ? " set" : " sets"));
        tv_displayName.setText(currentUser.getDisplayName());
        tv_folderName.setText(currentFolder.getFolderName());
        tv_folderDescription.setText(currentFolder.getFolderDescription());
        Glide.with(this).load(currentUser.getPhotoUrl()).error(R.drawable.default_user_image)
                .into(img_AccountImage);
        if (numberOfSets == 0) {
            rv_studySets.setVisibility(View.INVISIBLE);
        } else
            noSet.setVisibility(View.INVISIBLE);

    }

    void initData() {
        Bundle extras = getIntent().getExtras();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (extras != null) {
            folderID = extras.getString("folderID");
            database = FirebaseFirestore.getInstance();
            CollectionReference folderRef = database.collection("folders");

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
                                            ArrayList<StudySet> studySets = new ArrayList<>();
                                            for (DocumentSnapshot d : task.getResult()
                                            ) {
                                                StudySet set = d.toObject(StudySet.class);
                                                studySets.add(set);
                                                set.setStudySetID(d.getId());
                                            }
                                            currentFolder.setStudysets(studySets);
                                            numberOfSets = currentFolder.getStudysets().size();
                                            // load data to UI
                                            onDataLoaded();
                                        }

                                    });
                        }
                    });
        }
    }

    void bindingAction() {
        img_back.setOnClickListener(view -> {
            onBackPressed();
            onBackPressed();
        });
        img_kebab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_addset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FolderDetailActivity.this, CreateStudySetActivity.class);
                intent.putExtra("folderID", folderID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_folder);
        initUI();
        initData();
        bindingAction();
    }


}
