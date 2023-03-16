package com.example.demodatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.adapter.CreateTermAdapter;
import com.example.demodatabase.clickinterface.TermItemClickListener;
import com.example.demodatabase.fragments.AddSetToFolderFragment;
import com.example.demodatabase.fragments.ProfileClassFragment;
import com.example.demodatabase.fragments.ProfileFolderFragment;
import com.example.demodatabase.fragments.ProfileSetFragment;
import com.example.demodatabase.fragments.SearchFragment;
import com.example.demodatabase.model.StudySet;
import com.example.demodatabase.model.Term;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddSetToFolderActivity extends AppCompatActivity {

    FirebaseFirestore database;
    FirebaseUser currentUser;
    SweetAlertDialog sweetAlertDialog;
    String folderID;
    String filter = "created";
    Button btn_created, btn_searched, checkFinish;
    ImageView backView, addingTerm, btn_addNewSet;
    public static ArrayList<StudySet> selectedStudySets = new ArrayList<>();


    void init() {

        backView = findViewById(R.id.imv_back);
        btn_created = findViewById(R.id.btn_created);
        btn_searched = findViewById(R.id.btn_searched);
        btn_addNewSet = findViewById(R.id.btn_addnewset);
        checkFinish = findViewById(R.id.img_checkFinish);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseFirestore.getInstance();

        sweetAlertDialog = new SweetAlertDialog(this);
    }

    void initData() {
        Bundle extras = getIntent().getExtras();
        folderID = extras.getString("folderID");

    }


    void bindingAction() {

        backView.setOnClickListener(view -> {
            onBackPressed();
        });
        // Set back icon to back


        replaceFragment(new AddSetToFolderFragment());
        btn_created.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAll();
                lookSelected(btn_created);
                replaceFragment(new AddSetToFolderFragment());
            }
        });
        btn_searched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unselectAll();
                lookSelected(btn_searched);
                replaceFragment(new SearchFragment("multipleSelectedItems"));
            }
        });

        checkFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFolder(selectedStudySets);
            }
        });

        btn_addNewSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddSetToFolderActivity.this, CreateStudySetActivity.class);
                intent.putExtra("folderID", folderID);
                startActivity(intent);
            }
        });

    }

    private void addToFolder(ArrayList<StudySet> selectedStudySets) {
        for (StudySet selectedSet : selectedStudySets) {
            database.collection("folders").document(folderID).collection("studySets").
                    document(selectedSet.getStudySetID()).set(selectedSet);


        }
        new SweetAlertDialog(AddSetToFolderActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("Created successfully")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        selectedStudySets.clear();
                        Intent intent = new Intent(AddSetToFolderActivity.this, FolderDetailActivity.class);
                        intent.putExtra("folderID", folderID);
                        startActivity(intent);

                    }
                })
                .show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_set_to_folder);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            folderID = extras.getString("folderID");
        }
        replaceFragment(new ProfileFolderFragment());
        init();
        initData();
        bindingAction();


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = AddSetToFolderActivity.this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.create_folder, fragment).commit();

    }

    void lookSelected(Button clickedButton) {
        clickedButton.setBackgroundColor(ContextCompat.getColor(AddSetToFolderActivity.this, R.color.primary_1));
        clickedButton.setTextColor(ContextCompat.getColor(AddSetToFolderActivity.this, R.color.white));
    }

    void lookUnselected(Button clickedButton) {
        clickedButton.setBackgroundColor(ContextCompat.getColor(AddSetToFolderActivity.this, R.color.white));
        clickedButton.setTextColor(ContextCompat.getColor(AddSetToFolderActivity.this, R.color.black));
    }

    void unselectAll() {
        lookUnselected(btn_created);
        lookUnselected(btn_searched);
    }


}
