package com.example.demodatabase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.adapter.StudySetAdapter;
import com.example.demodatabase.databinding.ActivityFolderDetailKebabMenuBinding;
import com.example.demodatabase.fragments.FolderDetailFragment;
import com.example.demodatabase.model.Folder;
import com.example.demodatabase.model.StudySet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FolderDetailActivity extends AppCompatActivity {
    ActivityFolderDetailKebabMenuBinding activityFolderDetailKebabMenuBinding;
//    RecyclerView rv_studySets;
//    CardView noSet;
    FirebaseFirestore database;
    FirebaseUser currentUser;
    private ArrayList<StudySet> studySets = new ArrayList<>();
//    SweetAlertDialog sweetAlertDialog;
//    TextView tv_numberOfSets, tv_displayName, tv_folderName, tv_folderDescription;
    ImageView img_AccountImage, img_back, img_kebab_menu;
    String folderID;
    Folder currentFolder;
//    int numberOfSets;
//    Button btn_addset;
//    StudySetAdapter studySetAdapter;
    boolean isUpdated = false;


    private void initData() {
        Bundle extras = getIntent().getExtras();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        folderID = extras.getString("folderID");
        database = FirebaseFirestore.getInstance();

        img_back = findViewById(R.id.imv_back);
        img_kebab_menu = findViewById(R.id.img_kebab_menu);
        activityFolderDetailKebabMenuBinding.kebabMenu.setVisibility(View.GONE);
        database.collection("folders").document(folderID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                currentFolder = task.getResult().toObject(Folder.class);
                currentFolder.setFolderID(task.getResult().getId());
                System.out.println(currentFolder.getFolderName());

            }
        });


    }

    void bindingAction() {

        activityFolderDetailKebabMenuBinding.conLayOptOut.setOnClickListener(view -> {
//            slideUp(activityMainBinding.bottomNavigation);
            slideDown(activityFolderDetailKebabMenuBinding.kebabMenu);
        });

        activityFolderDetailKebabMenuBinding.tvAddset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(activityFolderDetailKebabMenuBinding.kebabMenu);
//                Intent intent = new Intent(FolderDetailActivity.this, CreateStudySetActivity.class);
//                intent.putExtra("folderID", folderID);
//                startActivity(intent);

                Intent intent = new Intent(FolderDetailActivity.this, AddSetToFolderActivity.class);
                intent.putExtra("folderID", folderID);
                startActivity(intent);
            }
        });

        activityFolderDetailKebabMenuBinding.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("vao delete");
                slideDown(activityFolderDetailKebabMenuBinding.kebabMenu);
                AlertDialog.Builder builder = new AlertDialog.Builder(FolderDetailActivity.this);
                builder.setTitle("");
                builder.setMessage("Are you sure you want to delete this folder permanently? The sets inside will not be deleted.");

                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        CollectionReference folderRef = database.collection("folders");
                        folderRef.document(folderID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(FolderDetailActivity.this, "Deleted Successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("markedTab", "1");
                                startActivity(intent);
                            }
                        });
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        activityFolderDetailKebabMenuBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click cancel");
                slideDown(activityFolderDetailKebabMenuBinding.kebabMenu);

            }
        });

        activityFolderDetailKebabMenuBinding.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("edit o trong acitivity");
                slideDown(activityFolderDetailKebabMenuBinding.kebabMenu);
                editFolder(v);
            }
        });



        img_back.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("markedTab", "1");
            startActivity(intent);
        });
        img_kebab_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click kebab menu nek");
                slideUp(activityFolderDetailKebabMenuBinding.kebabMenu);
                activityFolderDetailKebabMenuBinding.kebabMenu.bringToFront();
                System.out.println(activityFolderDetailKebabMenuBinding.tvDelete);
            }
        });
//        btn_addset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FolderDetailActivity.this, CreateStudySetActivity.class);
//                intent.putExtra("folderID", folderID);
//                startActivity(intent);
//
//
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_folder_detail_kebab_menu);
        activityFolderDetailKebabMenuBinding = ActivityFolderDetailKebabMenuBinding.inflate(getLayoutInflater());
        setContentView(activityFolderDetailKebabMenuBinding.getRoot());
        replaceFragment(new FolderDetailFragment());
        initData();
        bindingAction();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public void slideUp(View view) {
        System.out.println("vao slide up ne");

        System.out.println(view);
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
        view.setVisibility(view.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(false);
        view.startAnimation(animate);
    }

    public void editFolder(View view) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_edit_folder, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button btn_ok, btn_cancel;
        btn_ok = popupView.findViewById(R.id.btn_ok);
        btn_cancel = popupView.findViewById(R.id.btn_cancel);
        EditText edt_folderName, edt_folderDescription;
        edt_folderName = popupView.findViewById(R.id.edt_foldername);
        edt_folderDescription = popupView.findViewById(R.id.edt_description);

        edt_folderName.setText(currentFolder.getFolderName());
        edt_folderDescription.setText(currentFolder.getFolderDescription());

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName = edt_folderName.getText().toString();
                String folderDescription = edt_folderDescription.getText().toString();

                if (folderName.trim().length() == 0) {
                    new SweetAlertDialog(FolderDetailActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Your folder must have a name!")
                            .show();
                    return;
                }

                if(!folderName.equals(currentFolder.getFolderName())){
                    database.collection("folders").document(folderID).update("folderName", folderName).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("update folder name", "DocumentSnapshot successfully updated");
                            isUpdated = true;
                        }
                    });
                }
                if(!folderDescription.equals(currentFolder.getFolderDescription())){
                    database.collection("folders").document(folderID).update("folderDescription", folderDescription).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("udfd", "DocumentSnapshot successfully updted");
                            isUpdated = true;
                        }
                    });
                }
                Toast.makeText(FolderDetailActivity.this, "Edited Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), FolderDetailActivity.class);
                intent.putExtra("folderID", folderID);
                startActivity(intent);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
    }

}
