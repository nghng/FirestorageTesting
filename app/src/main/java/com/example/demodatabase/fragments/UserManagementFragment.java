package com.example.demodatabase.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.adapter.ItemUserManageAdapter;
import com.example.demodatabase.clickinterface.ItemUserManageClickedListener;
import com.example.demodatabase.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UserManagementFragment extends Fragment {
    RecyclerView rcUser;
    ItemUserManageAdapter itemUserManageAdapter;
    ArrayList<User> users = new ArrayList<>();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    LinearLayoutManager layoutManager;
    EditText searchView;
    FirebaseAuth auth;
    boolean isSearch = true;





    Query query = database
            .collection("users")
            .orderBy("createdDate", Query.Direction.DESCENDING)
            .limit(10);


    void initUI(View view) {
        rcUser = view.findViewById(R.id.rc_users);
        searchView = view.findViewById(R.id.search);
        auth = FirebaseAuth.getInstance();
    }

    void onDataLoaded() {
        layoutManager = new LinearLayoutManager(getContext());
        rcUser.setLayoutManager(layoutManager);
        itemUserManageAdapter = new ItemUserManageAdapter(getContext(), users, new ItemUserManageClickedListener() {
            @Override
            public void resetPassword(User user, int pos) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Reset password")
                        .setContentText("Send a password reset email " + user.getEmail())
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                auth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                           sweetAlertDialog.cancel();
                                        } else {
                                            FancyToast.makeText(getContext(), "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                            }
                        })
                        .show();
                ;




            }

            @Override
            public void disableAccount(User user, int pos) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(user.getBan() ? "Enable this account" : "Disable this account")
                        .setTitleText(user.getBan() ? "This user will be able to login again" : "This user will no longer able to log in")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                database.collection("users")
                                        .document(user.getEmail())
                                        .update("isBan" , !user.getBan()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                user.setBan(!user.getBan());
                                                itemUserManageAdapter.notifyDataSetChanged();
                                                sweetAlertDialog.cancel();
                                            }
                                        });
                            }
                        })
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                            }
                        })
                        .show();


            }

            @Override
            public void deleteAccount(User user, int pos) {

            }
        });
        rcUser.setAdapter(itemUserManageAdapter);

        rcUser.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findLastCompletelyVisibleItemPosition() == users.size() - 1) {
                    // Fetch the next batch of data
                    if (users.size() > 0 && isSearch) {
                        query.startAfter(users.get(users.size() - 1).getCreatedDate())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        // Add the data to your adapter
                                        for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()
                                        ) {
                                            User u = d.toObject(User.class);
                                            u.setEmail(d.getId());
                                            users.add(u);
                                        }
                                        itemUserManageAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle the error
                                    }
                                });
                    }

                }
            }
        });

    }

    void onDataLoadedForQuery() {
        layoutManager = new LinearLayoutManager(getContext());
        rcUser.setLayoutManager(layoutManager);
        rcUser.setAdapter(itemUserManageAdapter);


    }

    void initData() {
        users.clear();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot d : task.getResult()
                ) {
                    User u = d.toObject(User.class);
                    u.setEmail(d.getId());
                    users.add(u);
                }
                onDataLoaded();
            }
        });


    }

    void bindingAction() {
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.toString().trim().equals("")) {
                    isSearch = true;
                    initData();
                }
                database.collection("users")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                users.clear();
                                itemUserManageAdapter.notifyDataSetChanged();
                                for (DocumentSnapshot d : task.getResult()
                                ) {
                                    if (d.getId().contains(s.toString().toLowerCase(Locale.ROOT))) {
                                        isSearch = false;
                                        User u = d.toObject(User.class);
                                        u.setEmail(d.getId());
                                        users.add(u);
                                    }
                                }
                                onDataLoadedForQuery();

                            }
                        });

            }
        });

    }

    public UserManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_management, container, false);
        initUI(view);
        initData();
        bindingAction();

        return view;
    }
}