package com.example.demodatabase.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.demodatabase.R;

public class AddStudySetFragment extends Fragment {


    public AddStudySetFragment() {
        // Required empty public constructor
    }


    void initUI(){

    }

    void initData(){
    }

    void bindingAction(){

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_study_set, container, false);
        initUI();
        initData();
        bindingAction();


        return view;
    }
}