package com.example.demodatabase.clickinterface;

import com.example.demodatabase.model.User;

public interface ItemUserManageClickedListener {
    void resetPassword(User user, int pos);

    void disableAccount(User user, int pos);

    void deleteAccount(User user, int pos);
}
