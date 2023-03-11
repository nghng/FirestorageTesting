package com.example.demodatabase.clickinterface;

import com.example.demodatabase.model.Term;

public interface EditTermClickedListener {
    void onDeleteClick(Term item, int pos);

    void onAddingClick(Term item, int pos);


}
