package com.example.demodatabase.adapter;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.clickinterface.EditTermClickedListener;
import com.example.demodatabase.clickinterface.TermItemClickListener;
import com.example.demodatabase.model.Term;

import java.util.ArrayList;

public class EditTermAdapter extends RecyclerView.Adapter<EditTermAdapter.CreateStudySetViewHolder> {
    private ArrayList<Term> terms;
    public Context mContext;
    private final EditTermClickedListener listener;

    public EditTermAdapter(ArrayList<Term> terms, Context context, EditTermClickedListener listener) {
        this.terms = terms;
        this.listener = listener;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CreateStudySetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_create_studyset, parent, false);
        return new CreateStudySetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateStudySetViewHolder holder, int position) {
        int pos = position;
        holder.bind(terms.get(pos), listener);
        holder.etTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {

                        terms.get(pos).setTerm(holder.etTerm.getText().toString());
                        Log.d("textchange", "afterTextChanged: " + terms.get(pos).toString());

                }catch (Exception e){

                }



            }
        });

        holder.etDefinition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    terms.get(pos).setDefinition(holder.etDefinition.getText().toString());
                    Log.d("textchange", "afterTextChanged: " + terms.get(pos).toString());
                }catch (Exception e){

                }



            }
        });


//        holder.addTerm.setOnClickListener(view -> {
//            listener.onAddingClick(terms.get(pos), pos);
//            Term newTerm = new Term("","");
//            terms.add(pos, newTerm);
//            notifyDataSetChanged();
//        });

        holder.removeTerm.setOnClickListener(view ->{
            listener.onDeleteClick(terms.get(pos), pos);
            terms.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(0, terms.size());



        });

    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public class CreateStudySetViewHolder extends RecyclerView.ViewHolder {
        EditText etTerm, etDefinition;
        ImageView removeTerm, addTerm;


        public CreateStudySetViewHolder(@NonNull View itemView) {
            super(itemView);
            etTerm = itemView.findViewById(R.id.et_term);
            etDefinition = itemView.findViewById(R.id.et_definition);
            removeTerm = itemView.findViewById(R.id.imv_removeTerm);
//            addTerm = itemView.findViewById(R.id.imv_addTerm);

        }
        public void bind(Term term, EditTermClickedListener listener) {
            etTerm.setText(term.getTerm());
            etDefinition.setText(term.getDefinition());
        }


    }
}
