package com.example.demodatabase.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.clickinterface.OnItemClickedListener;
import com.example.demodatabase.model.StudySet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StudySetVerticalAdapter extends RecyclerView.Adapter<StudySetVerticalAdapter.StudySetViewHolder> implements Filterable {
    private ArrayList<StudySet> studySets;
    private ArrayList<StudySet> studySetsOld;
    public Context mContext;
    private final OnItemClickedListener onItemClickedListener;


    public StudySetVerticalAdapter(ArrayList<StudySet> studySets, Context mContext, OnItemClickedListener onItemClickedListener) {
        this.studySets = studySets;
        this.studySetsOld = studySets;
        this.mContext = mContext;
        this.onItemClickedListener = onItemClickedListener;
    }

//    @Override
//    public int getItemViewType(int type) {
//        return type;
//    }


    @NonNull
    @Override
    public StudySetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_studyset_vertical, parent, false);

        if (viewType != 0) {
            view.findViewById(R.id.cv_studyset_v).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

//        View view2= LayoutInflater.from(mContext).inflate(R.layout.fragment_search, parent, false);

//        System.out.println("item " + viewType);
        return new StudySetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudySetViewHolder holder, int position) {

//        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                holder.cardView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.my_border));
//                return false;
//            }
//        });
        if (position < studySets.size()) {
            holder.bind(studySets.get(position), onItemClickedListener);
            Drawable bg = holder.cardView.getBackground();
            ;
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!studySets.get(holder.getAdapterPosition()).isSelected()) {

                        holder.cardView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.my_border));
                    } else {
                        holder.cardView.setBackground(bg);
                    }
                    onItemClickedListener.onItemClick(studySets.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return studySets.size();
    }

    public ArrayList<StudySet> getResult(String search, String listfilter) {
        ArrayList<StudySet> list = new ArrayList<>();
        if (!search.isEmpty()) {
            if (listfilter.equalsIgnoreCase("set")) {
                for (StudySet studySet : studySetsOld) {
                    if (studySet.getStudySetName().toLowerCase().contains(search.toLowerCase())) {
                        list.add(studySet);
                    }
                }
            } else if (listfilter.equalsIgnoreCase("user")) {

                for (StudySet studySet : studySetsOld) {
                    if (studySet.getDisplayName() != null) {
                        if (studySet.getDisplayName().toLowerCase().contains(search.toLowerCase())) {
                            list.add(studySet);
                        }
                    }

                }
            } else {
                for (StudySet studySet : studySetsOld) {
                    if (studySet.getDisplayName() != null) {
                        if (studySet.getDisplayName().toLowerCase().contains(search.toLowerCase())
                                || studySet.getStudySetName().toLowerCase().contains(search.toLowerCase())) {
                            list.add(studySet);
                        }
                    }

                }
            }
            studySets = list;
            // notifyDataSetChanged();
        }
        return studySets;
    }

    public Filter getFilter(String filter) {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = constraint.toString();
                studySets = getResult(search, filter);
                FilterResults filterResults = new FilterResults();
                filterResults.values = studySets;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                studySets = (ArrayList<StudySet>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = constraint.toString();
                ArrayList<StudySet> list = new ArrayList<>();
                if (!search.isEmpty()) {
                    for (StudySet studySet : studySetsOld) {
                        if (studySet.getStudySetName().toLowerCase().contains(search.toLowerCase())) {
                            list.add(studySet);
                        }
                    }
                }
                studySets = list;
                FilterResults filterResults = new FilterResults();
                filterResults.values = studySets;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                studySets = (ArrayList<StudySet>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class StudySetViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudySetName;
        TextView tvNumberOfTerms;
        ImageView imvAccountImage;
        TextView tvAccountName;
        CardView cardView;

        public StudySetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAccountName = itemView.findViewById(R.id.tv_accountName_v);
            imvAccountImage = itemView.findViewById(R.id.imv_accountImage_v);
            tvNumberOfTerms = itemView.findViewById(R.id.tv_numberOfTerms_v);
            tvStudySetName = itemView.findViewById(R.id.tv_studySetName_v);
            cardView = itemView.findViewById(R.id.cv_studyset_v);
        }

        public void bind(StudySet studySet, OnItemClickedListener listener) {
            tvStudySetName.setText(studySet.getStudySetName());
            tvAccountName.setText(studySet.getDisplayName());
            Uri photoURL = Uri.parse(studySet.getImageUri());
            Picasso.get().load(photoURL).into(imvAccountImage);

            if (studySet.getTerms() != null) {
                tvNumberOfTerms.setText(studySet.getTerms().size() + " terms");
            } else {
                tvNumberOfTerms.setText("No terms created yet");

            }


        }
    }


}
