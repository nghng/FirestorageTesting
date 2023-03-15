package com.example.demodatabase.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, terms.get(holder.getAdapterPosition()).getTerm(),terms.get(holder.getAdapterPosition()).getDefinition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }


    public class ItemTermCardViewHolder extends RecyclerView.ViewHolder {
        TextView termInCard, definitionInCard;
        CardView cardTerm;

        public ItemTermCardViewHolder(@NonNull View itemView) {
            super(itemView);
            termInCard = itemView.findViewById(R.id.tv_termInCard);
            definitionInCard = itemView.findViewById(R.id.tv_definitionInCard);
            cardTerm = itemView.findViewById(R.id.card_term);
        }

        void bind(Term term) {
            termInCard.setText(term.getTerm());
            definitionInCard.setText(term.getDefinition());
        }

    }

    public void showPopup(View view, String term, String definition) {
        TextView popup_card_term, popup_card_definition;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_card_term, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popup_card_term = popupView.findViewById(R.id.textView_popup_term);
        popup_card_definition = popupView.findViewById(R.id.textView_popup_definition);

        popup_card_term.setText(term);
        popup_card_definition.setText(definition);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
