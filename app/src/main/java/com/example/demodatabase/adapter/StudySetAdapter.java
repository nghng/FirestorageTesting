package com.example.demodatabase.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.fragments.ProfileSetFragment;
import com.example.demodatabase.model.StudySet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StudySetAdapter extends RecyclerView.Adapter<StudySetAdapter.StudySetViewHolder> {
    private ArrayList<StudySet> studySets;
    public Context mContext;
    private final OnItemClickedListener onItemClickedListener;



    public StudySetAdapter(ArrayList<StudySet> studySets, Context mContext, OnItemClickedListener onItemClickedListener) {
        this.studySets = studySets;
        this.mContext = mContext;
        this.onItemClickedListener = onItemClickedListener;

    }

    @NonNull
    @Override
    public StudySetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_studyset, parent, false);

        if(viewType==1){
            view.findViewById(R.id.cv_studyset).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        System.out.println(viewType);
        return new StudySetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudySetViewHolder holder, int position) {
        holder.bind(studySets.get(position), onItemClickedListener);


//        holder.cart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onItemClick(guests.get(position), position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return studySets.size();
    }


    public class StudySetViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudySetName;
        TextView tvNumberOfTerms;
        ImageView imvAccountImage;
        TextView tvAccountName;

        public StudySetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAccountName = itemView.findViewById(R.id.tv_accountName);
            imvAccountImage = itemView.findViewById(R.id.imv_accountImage);
            tvNumberOfTerms = itemView.findViewById(R.id.tv_numberOfTerms);
            tvStudySetName = itemView.findViewById(R.id.tv_studySetName);
        }

        public void bind(StudySet studySet, OnItemClickedListener listener) {
            tvStudySetName.setText(studySet.getStudySetName());
            tvAccountName.setText(studySet.getDisplayName());
            Uri photoURL = Uri.parse(studySet.getImageUri());
            Picasso.get().load(photoURL).into(imvAccountImage);
            if(studySet.getTerms() != null){
                tvNumberOfTerms.setText(studySet.getTerms().size() + " ");
            }else {
                tvNumberOfTerms.setText("No terms created yet");

            }

        }
    }


}
