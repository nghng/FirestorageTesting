package com.example.demodatabase.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demodatabase.AddSetToFolderActivity;
import com.example.demodatabase.FolderDetailActivity;
import com.example.demodatabase.MainActivity;
import com.example.demodatabase.R;
import com.example.demodatabase.StudySetDetailActivity;
import com.example.demodatabase.adapter.StudySetAdapter;
import com.example.demodatabase.adapter.StudySetVerticalAdapter;
import com.example.demodatabase.clickinterface.OnItemClickedListener;
import com.example.demodatabase.databinding.ActivityFolderDetailKebabMenuBinding;
import com.example.demodatabase.model.Folder;
import com.example.demodatabase.model.StudySet;
import com.example.demodatabase.model.Term;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FolderDetailFragment extends Fragment {
    ActivityFolderDetailKebabMenuBinding activityFolderDetailKebabMenuBinding;
    RecyclerView rv_studySets;
    CardView noSet;
    FirebaseFirestore database;
    FirebaseUser currentUser;
    private ArrayList<StudySet> studySets = new ArrayList<>();
    SweetAlertDialog sweetAlertDialog;
    TextView tv_numberOfSets, tv_displayName, tv_folderName, tv_folderDescription;
    ImageView img_AccountImage, img_back, img_kebab_menu;
    String folderID;
    Folder currentFolder;
    int numberOfSets;
    Button btn_addset;
    StudySetVerticalAdapter studySetAdapter;
    ItemTouchHelper itemTouchHelper;

    public FolderDetailFragment() {
        // Required empty public constructor
    }

    private void initUI(View view) {
        img_back = view.findViewById(R.id.imv_back);
        tv_numberOfSets = view.findViewById(R.id.tv_numberOfSets);
        tv_displayName = view.findViewById(R.id.tv_displayName);
        tv_folderName = view.findViewById(R.id.tv_folderName);
        tv_folderDescription = view.findViewById(R.id.tv_folderDescription);
        img_AccountImage = view.findViewById(R.id.imv_accountImage);
        noSet = view.findViewById(R.id.cv_noset);
        rv_studySets = view.findViewById(R.id.rv_studySets);
        img_kebab_menu = view.findViewById(R.id.img_kebab_menu);
        btn_addset = view.findViewById(R.id.btn_addset);
//        activityFolderDetailKebabMenuBinding.kebabMenu.setVisibility(View.GONE);
    }

    private void initData() {
        Bundle extras = getActivity().getIntent().getExtras();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (extras != null) {
            folderID = extras.getString("folderID");
            System.out.println(folderID);
            database = FirebaseFirestore.getInstance();
            CollectionReference studySetRef = database.collection("studySets");
            CollectionReference folderRef = database.collection("folders");
            folderRef.document(folderID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            currentFolder = task.getResult().toObject(Folder.class);

                            tv_displayName.setText(currentUser.getDisplayName());
                            tv_folderName.setText(currentFolder.getFolderName());
                            tv_folderDescription.setText(currentFolder.getFolderDescription());
                            Glide.with(getContext()).load(currentUser.getPhotoUrl()).error(R.drawable.default_user_image)
                                    .into(img_AccountImage);

                            folderRef.document(folderID)
                                    .collection("studySets")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (DocumentSnapshot d : task.getResult()
                                            ) {
                                                ArrayList<Term> terms = new ArrayList<>();
                                                database.collection("studySets")
                                                        .document(d.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                StudySet studySet = task.getResult().toObject(StudySet.class);
                                                                studySet.setStudySetID(task.getResult().getId());
                                                                studySetRef.document(studySet.getStudySetID()).collection("terms")
                                                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                for (DocumentSnapshot d : task.getResult().getDocuments()
                                                                                ) {
                                                                                    Term t = d.toObject(Term.class);
                                                                                    terms.add(t);
                                                                                }

                                                                                studySet.setTerms(terms);
                                                                                studySets.add(studySet);
                                                                                onDataLoaded();
                                                                            }
                                                                        });
                                                            }
                                                        });

                                            }
                                            // load data to UI
                                        }
                                    });
                        }
                    });


        }
    }


    void onDataLoaded() {
        currentFolder.setStudysets(studySets);
        numberOfSets = currentFolder.getStudysets().size();

        tv_numberOfSets.setText(numberOfSets + (numberOfSets <= 1 ? " set" : " sets"));

        if (numberOfSets == 0) {
            rv_studySets.setVisibility(View.INVISIBLE);
        } else {
            noSet.setVisibility(View.INVISIBLE);
            rv_studySets.setVisibility(View.VISIBLE);

            studySetAdapter = new StudySetVerticalAdapter(currentFolder.getStudysets(), getActivity(), new OnItemClickedListener() {
                @Override
                public void onItemClick(StudySet item, int pos) {
                    Intent intent = new Intent(getActivity(), StudySetDetailActivity.class);
                    intent.putExtra("studySetID", item.getStudySetID());
                    startActivity(intent);
                    ;
                }

            });
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rv_studySets.setLayoutManager(layoutManager);
            rv_studySets.setAdapter(studySetAdapter);

            ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position=viewHolder.getAdapterPosition();
                    StudySet removingSet=studySets.get(position);
                    CollectionReference folderRef = database.collection("folders");
                            folderRef.document(folderID).collection("studySets").document(removingSet.getStudySetID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });

                    Snackbar snackbar = Snackbar
                            .make(rv_studySets, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                    studySets.remove(position);
                    studySetAdapter.notifyItemRemoved(position);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            folderRef.document(folderID).collection("studySets").
                                    document(removingSet.getStudySetID()).set(removingSet);
                            Toast.makeText(getContext(), "Undo Successfully", Toast.LENGTH_LONG).show();
//                                    Intent intent = new Intent(getContext(), FolderDetailActivity.class);
//                                    intent.putExtra("folderID", folderID);
//                                    startActivity(intent);
                            studySets.add(position, removingSet);
                            studySetAdapter.notifyItemInserted(position);
                        }
                    });

//                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();


//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    builder.setTitle("");
//                    builder.setMessage("Are you sure you want to remove this set from this folder permanently?");
//
//                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
//
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            CollectionReference folderRef = database.collection("folders");
//                            folderRef.document(folderID).collection("studySets").document(removingSet.getStudySetID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Toast.makeText(getContext(), "Removed Successfully", Toast.LENGTH_LONG).show();
//                                    Intent intent = new Intent(getContext(), FolderDetailActivity.class);
//                                    intent.putExtra("folderID", folderID);
//                                    startActivity(intent);
//                                }
//                            });
//                            dialog.dismiss();
//
//                        }
//                    });
//
//                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            // Do nothing
//                            dialog.dismiss();
//                        }
//                    });
//
//                    AlertDialog alert = builder.create();
//                    alert.show();
//                    studySets.remove(position);
//                    studySetAdapter.notifyItemRemoved(position);
                }
            };

            itemTouchHelper=new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(rv_studySets);
        }
    }

    void bindingAction() {


        btn_addset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), CreateStudySetActivity.class);
//                intent.putExtra("folderID", folderID);
//                startActivity(intent);

                Intent intent = new Intent(getActivity(), AddSetToFolderActivity.class);
                intent.putExtra("folderID", folderID);
                startActivity(intent);
            }
        });


//        img_kebab_menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("click kebab menu nek");
//                slideUp(activityFolderDetailKebabMenuBinding.kebabMenu);
//                activityFolderDetailKebabMenuBinding.kebabMenu.bringToFront();
//            }
//        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_create_folder, container, false);
        initUI(view);
        initData();
        bindingAction();
        return view;

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
}