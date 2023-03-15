package com.example.demodatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


import com.example.demodatabase.databinding.ActivityMainBinding;
import com.example.demodatabase.fragments.HomeFragment;
import com.example.demodatabase.fragments.ProfileFragment;
import com.example.demodatabase.fragments.SearchFragment;
import com.example.demodatabase.model.Folder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;
    int previousSelectedMenu;
    FirebaseFirestore database;
    int markedTab=0;

    private void initData() {
        activityMainBinding.wrapper.setVisibility(View.GONE);

    }

    void bindingAction() {
        activityMainBinding.conLayOptOut.setOnClickListener(view -> {
//            slideUp(activityMainBinding.bottomNavigation);
            slideDown(activityMainBinding.wrapper);
            activityMainBinding.bottomNavigation.setVisibility(View.VISIBLE);
//            activityMainBinding.wrapper.setVisibility(View.GONE);
            activityMainBinding.bottomNavigation.setSelectedItemId(previousSelectedMenu);
        });

        activityMainBinding.tvCreateStudySets.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateStudySetActivity.class);
            startActivity(intent);
        });

        activityMainBinding.tvCreateFolder.setOnClickListener(view -> {
            slideDown(activityMainBinding.wrapper);
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_create_folder, null);

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


            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String folderName = edt_folderName.getText().toString();
                    String folderDescription = edt_folderDescription.getText().toString();
                    if (folderName.trim().length() == 0) {
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setContentText("Your folder must have a name!")
                                .show();
                        return;
                    }
                    Intent intent = new Intent(MainActivity.this, CreateFolderActivity.class);
                                            intent.putExtra("folderName", folderName);
                    intent.putExtra("folderDescription", folderDescription);
                                            startActivity(intent);
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    slideUp(activityMainBinding.bottomNavigation);
                }
            });

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        Bundle extra=getIntent().getExtras();
        if(extra!=null)
            markedTab=Integer.parseInt(extra.getString("markedTab"));
        System.out.println("marked tab ne: "+markedTab);

if(markedTab==0){
    replaceFragment(new HomeFragment());
    initData();
    bindingAction();
    activityMainBinding.bottomNavigation.setOnItemSelectedListener(item -> {
        switch (item.getItemId()) {
            case R.id.home:
                replaceFragment(new HomeFragment());
                previousSelectedMenu = R.id.home;
                break;

            case R.id.addStudySet:
                // show adding selections
                slideUp(activityMainBinding.wrapper);
//                    activityMainBinding.wrapper.setVisibility(View.VISIBLE);
                activityMainBinding.bottomNavigation.setVisibility(View.GONE);
                activityMainBinding.wrapper.bringToFront();
                break;

            case R.id.search:
                previousSelectedMenu = R.id.search;
                replaceFragment(new SearchFragment());
                break;

            case R.id.profile:
                previousSelectedMenu = R.id.profile;
                replaceFragment(new ProfileFragment());
                break;
        }

        return true;
    });
}
else{
    activityMainBinding.bottomNavigation.setSelectedItemId(R.id.profile);
    replaceFragment(new ProfileFragment(markedTab));
    markedTab=0;
    initData();
    bindingAction();
}



    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    //    Animation
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
}