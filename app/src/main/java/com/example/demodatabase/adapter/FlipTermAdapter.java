package com.example.demodatabase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.demodatabase.R;
import com.example.demodatabase.model.Term;

import java.util.ArrayList;

public class FlipTermAdapter extends PagerAdapter {
    private ArrayList<Term> terms;
    private Context mContext;

    public FlipTermAdapter(ArrayList<Term> terms, Context mContext) {
        this.terms = terms;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_flip_term, container, false);
        TextView tvFrontTerm, tvBackDefinition;
        tvFrontTerm = view.findViewById(R.id.tv_frontTerm);
        tvBackDefinition = view.findViewById(R.id.tv_backDefinition);
        Term term = terms.get(position);
        tvFrontTerm.setText(term.getTerm());
        tvBackDefinition.setText(term.getDefinition());
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        if (terms != null) {
            return terms.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public class FlipTermViewHolder extends RecyclerView.ViewHolder {
        TextView tvFrontTerm, tvBackDefinition;

        public FlipTermViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFrontTerm = itemView.findViewById(R.id.tv_frontTerm);
            tvBackDefinition = itemView.findViewById(R.id.tv_backDefinition);
        }

        public void bind(Term term) {
            tvFrontTerm.setText(term.getTerm());
            tvBackDefinition.setText(term.getDefinition());
        }
    }


}
