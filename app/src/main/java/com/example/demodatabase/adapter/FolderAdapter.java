package com.example.demodatabase.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.clickinterface.FolderItemClickListener;
import com.example.demodatabase.clickinterface.OnItemClickedListener;
import com.example.demodatabase.model.Folder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    private ArrayList<Folder> folders;
    public Context mContext;
    private final FolderItemClickListener onItemClickedListener;


    public FolderAdapter(ArrayList<Folder> folders, Context mContext, FolderItemClickListener onItemClickedListener) {

        this.folders = folders;
        this.mContext = mContext;
        this.onItemClickedListener = onItemClickedListener;

    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("oncreateviewholder");
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_folder, parent, false);
        System.out.println("oncreateviewholder");
        System.out.println(view);
        return new FolderAdapter.FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        holder.bind(folders.get(position), onItemClickedListener);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickedListener.onItemFolderClick(folders.get(holder.getAdapterPosition()),holder.getAdapterPosition());

            }
        });
        System.out.println("bind view holder");
        System.out.println(folders.get(position).getFolderName());
        if (position < folders.size())
            holder.bind(folders.get(position), onItemClickedListener);

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView tv_folderName;
        ImageView imv_accountImage;
        TextView tv_accountName;
        CardView cardView;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv_folder);
            tv_folderName = itemView.findViewById(R.id.tv_folderName);
            imv_accountImage = itemView.findViewById(R.id.imv_accountImage);
            tv_accountName = itemView.findViewById(R.id.tv_accountName);

        }

        public void bind(Folder folder, FolderItemClickListener listener) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            tv_folderName.setText(folder.getFolderName());
            tv_accountName.setText(folder.getDisplayName());
            Uri photoURL = currentUser.getPhotoUrl();
            Picasso.get().load(photoURL).into(imv_accountImage);


        }
    }
}