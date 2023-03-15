package com.example.demodatabase.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.clickinterface.ItemUserManageClickedListener;
import com.example.demodatabase.metadata.Role;
import com.example.demodatabase.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ItemUserManageAdapter extends RecyclerView.Adapter<ItemUserManageAdapter.ItemUserManageViewHolder> {
    private Context mContext;
    private ArrayList<User> users;
    private ItemUserManageClickedListener listener;


    public ItemUserManageAdapter(Context mContext, ArrayList<User> users, ItemUserManageClickedListener listener) {
        this.mContext = mContext;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemUserManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_manage, parent, false);
        return new ItemUserManageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemUserManageViewHolder holder, int position) {
        holder.bind(users.get(position));
        String[] paths = {" ", "Reset password", "Disable account"};
        String[] paths1 = {" ", "Reset password", "Enable account"};
        ArrayAdapter<String> adapter;

        if (users.get(holder.getAdapterPosition()).getBan()) {
            adapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_spinner_item, paths1);
            holder.card.setBackgroundColor(Color.RED);

        } else {
            adapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_spinner_item, paths);
            holder.card.setBackgroundColor(Color.WHITE);

        }
        if(users.get(holder.getAdapterPosition()).getRole() != Role.ADMIN_ROLE){
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(adapter);
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    switch (pos) {
                        case 1:
                            listener.resetPassword(users.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                            holder.spinner.setSelection(0);

                            break;

                        case 2:
                            listener.disableAccount(users.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                            holder.spinner.setSelection(0);
                            break;

                        case 3:
                            listener.deleteAccount(users.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                            holder.spinner.setSelection(0);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else {
            holder.spinner.setVisibility(View.INVISIBLE);
            holder.card.setBackgroundColor(Color.GREEN);
        }




    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ItemUserManageViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvCreatedDate, tvRole;
        Spinner spinner;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        CardView card;

        public ItemUserManageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvCreatedDate = itemView.findViewById(R.id.tv_createdDate);
            spinner = itemView.findViewById(R.id.spinner);
            card = itemView.findViewById(R.id.card);
        }

        void bind(User user) {
            tvEmail.setText(user.getEmail());
            tvCreatedDate.setText("Created date: " + dateFormat.format(user.getCreatedDate()));
            if (user.getRole() == Role.ADMIN_ROLE) {
                tvRole.setText("Role: Admin");
            } else if (user.getRole() == Role.USER_ROLE) {
                tvRole.setText("Role: User");
            }

        }
    }
}
