package com.example.demodatabase.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demodatabase.R;
import com.example.demodatabase.clickinterface.AnswerItemClickedListener;
import com.example.demodatabase.model.Answer;

import java.util.ArrayList;

public class ItemAnswerAdapter extends RecyclerView.Adapter<ItemAnswerAdapter.ItermAnswerViewHolder> {
    private Context mContext;
    private ArrayList<Answer> answers;
    private AnswerItemClickedListener listener;
    private int counter = 0;


    public ItemAnswerAdapter(Context mContext, ArrayList<Answer> answers, AnswerItemClickedListener listener) {
        this.mContext = mContext;
        this.answers = answers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItermAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_answer, parent, false);
        return new ItermAnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItermAnswerViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        holder.bind(answers.get(pos));
        Answer answer = answers.get(pos);
        if (answer.isRight()) {
            holder.cardAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRightAnswerClick(answer, pos);
                }
            });
        } else {
            holder.cardAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onWrongAnswerClick(answer, pos);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public class ItermAnswerViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnswer, tvChoice;
        CardView cardAnswer;

        public ItermAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAnswer = itemView.findViewById(R.id.tv_answer);
            tvChoice = itemView.findViewById(R.id.tv_choice);
            cardAnswer = itemView.findViewById(R.id.card_answer);
            cardAnswer.setForeground(null);
        }

        public void bind(Answer answer) {
            tvAnswer.setText(answer.getAnswer());
            System.out.println();
            tvChoice.setText(String.valueOf((char) (counter+64)));
            counter++;
            if(counter > answers.size()){
                counter = 1;
            }
        }
    }


}
