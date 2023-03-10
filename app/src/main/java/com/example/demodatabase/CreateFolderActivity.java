package com.example.demodatabase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demodatabase.model.Folder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CreateFolderActivity extends AppCompatActivity {
    Folder folder;
    FirebaseFirestore database;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        folder=new Folder(extras.getString("folderName"), extras.getString("folderDescription"));
        database = FirebaseFirestore.getInstance();
        database.collection("folders").add(folder).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Intent intent = new Intent(CreateFolderActivity.this, FolderDetailActivity.class);
                intent.putExtra("folderID", task.getResult().getId());
                startActivity(intent);
            }


                    });
    }
}
