//package com.example.demodatabase;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.demodatabase.adapter.CreateTermAdapter;
//import com.example.demodatabase.clickinterface.TermItemClickListener;
//import com.example.demodatabase.fragments.ProfileClassFragment;
//import com.example.demodatabase.fragments.ProfileFolderFragment;
//import com.example.demodatabase.fragments.ProfileSetFragment;
//import com.example.demodatabase.fragments.SearchFragment;
//import com.example.demodatabase.model.StudySet;
//import com.example.demodatabase.model.Term;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//import cn.pedant.SweetAlert.SweetAlertDialog;
//
//public class AddSetToFolderActivity extends AppCompatActivity {
//
//    FirebaseFirestore database;
//    FirebaseUser currentUser;
//    SweetAlertDialog sweetAlertDialog;
//    String folderID;
//    String filter="created";
//    Button btn_created, btn_searched;
//    ImageView backView, addingTerm, checkFinish;
//
//    void init() {
//        backView = findViewById(R.id.imv_back);
//       btn_created=findViewById(R.id.btn_created);
//       btn_searched=findViewById(R.id.btn_searched);
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//    }
//
//    void initData() {
//        Bundle extras = getIntent().getExtras();
//        folderID= extras.getString("folderID");
//
//    }
//
//
//    void bindingAction() {
//
//        backView.setOnClickListener(view -> {
//            onBackPressed();
//        });
//        // Set back icon to back
//
//
//        replaceFragment(new ProfileFolderFragment());
//        btn_created.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new ProfileFolderFragment());
//            }
//        });
//        btn_searched.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new SearchFragment());
//            }
//        });
//
//
//
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_study_set);
//        init();
//        initData();
//        bindingAction();
//
//
//    }
//
//    private void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = AddSetToFolderActivity.this.getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.switchMenuProfile, fragment).commit();
//
//    }
//}
