package com.example.demodatabase.adapter;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.clickinterface.OnItemClickedListener;
import com.example.demodatabase.model.Folder;
import com.example.demodatabase.model.StudySet;
import com.squareup.picasso.Picasso;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView tv_folderName;
        ImageView imvAccountImage;
        TextView tvAccountName;
        TextView tvNumberOfTerms;


        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
           tv_folderName=itemView.findViewById(R.id.tv_folderName);


        }

        public void bind(Folder folder, OnItemClickedListener listener) {
//            tvStudySetName.setText(studySet.getStudySetName());
//            tvAccountName.setText(studySet.getDisplayName());
//            Uri photoURL = Uri.parse(studySet.getImageUri());
//            Picasso.get().load(photoURL).into(imvAccountImage);
//
//            if (studySet.getTerms() != null) {
//                tvNumberOfTerms.setText(studySet.getTerms().size() + " terms");
//            } else {
//                tvNumberOfTerms.setText("No terms created yet");
//
//            }

        }
    }
}