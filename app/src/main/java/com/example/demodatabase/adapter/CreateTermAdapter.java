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
import com.example.demodatabase.clickinterface.TermItemClickListener;
import com.example.demodatabase.model.Term;

import java.util.ArrayList;

public class CreateTermAdapter extends RecyclerView.Adapter<CreateTermAdapter.CreateStudySetViewHolder> {
    private ArrayList<Term> terms;
    public Context mContext;
    private final TermItemClickListener listener;

    public CreateTermAdapter(ArrayList<Term> terms, Context context, TermItemClickListener listener) {
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
        int pos = holder.getBindingAdapterPosition();
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
                terms.get(pos).setTerm(holder.etTerm.getText().toString());
                Log.d("textchange", "afterTextChanged: " + terms.get(pos).toString());

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
                terms.get(pos).setDefinition(holder.etDefinition.getText().toString());
                Log.d("textchange", "afterTextChanged: " + terms.get(pos).toString());
            }
        });


//        holder.addTerm.setOnClickListener(view -> {
//            Term newTerm = new Term("","");
//            terms.add(pos, newTerm);
//            notifyDataSetChanged();
//        });

        holder.removeTerm.setOnClickListener(view ->{
            terms.remove(pos);
            notifyItemRemoved(pos);
        });

    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public class CreateStudySetViewHolder extends RecyclerView.ViewHolder {
        EditText etTerm, etDefinition;
        ImageView removeTerm;


        public CreateStudySetViewHolder(@NonNull View itemView) {
            super(itemView);
            etTerm = itemView.findViewById(R.id.et_term);
            etDefinition = itemView.findViewById(R.id.et_definition);
            removeTerm = itemView.findViewById(R.id.imv_removeTerm);
//            addTerm = itemView.findViewById(R.id.imv_addTerm);

        }
        public void bind(Term term, TermItemClickListener listener) {
            etTerm.setText(term.getTerm());
            etDefinition.setText(term.getDefinition());
        }


    }
}
