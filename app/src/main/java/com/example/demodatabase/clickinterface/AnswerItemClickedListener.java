package com.example.demodatabase.clickinterface;

import com.example.demodatabase.model.Answer;

public interface AnswerItemClickedListener {
    void onRightAnswerClick(Answer item, int pos);

    void onWrongAnswerClick(Answer item, int pos);
}
