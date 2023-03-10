package com.example.demodatabase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.model.Term;

import java.util.ArrayList;

public class ItemTermCardAdapter extends RecyclerView.Adapter<ItemTermCardAdapter.ItemTermCardViewHolder> {
    private Context mContext;
    private ArrayList<Term> terms;

    public ItemTermCardAdapter(Context mContext, ArrayList<Term> terms) {
        this.mContext = mContext;
        this.terms = terms;
    }

    @NonNull
    @Override
    public ItemTermCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_term_card_form, parent, false);
        return new ItemTermCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemTermCardViewHolder holder, int position) {
        holder.bind(terms.get(position));
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }


    public class ItemTermCardViewHolder extends RecyclerView.ViewHolder{
        TextView termInCard, definitionInCard;
        CardView cardTerm;
        public ItemTermCardViewHolder(@NonNull View itemView) {
            super(itemView);
            termInCard = itemView.findViewById(R.id.tv_termInCard);
            definitionInCard = itemView.findViewById(R.id.tv_definitionInCard);
            cardTerm = itemView.findViewById(R.id.card_term);
        }

        void bind(Term term){
            termInCard.setText(term.getTerm());
            definitionInCard.setText(term.getDefinition());
        }
    }
}
