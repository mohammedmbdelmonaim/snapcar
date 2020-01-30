package com.intcore.snapcar.ui.updatepassword;

import com.intcore.snapcar.core.base.BaseActivityScreen;

import okhttp3.ResponseBody;

public interface UpdatePasswordScreen extends BaseActivityScreen    {

    void setupEditText();

    void showConfirmPasswordErrorMessage(String messageEn);

    void newPasswordErrorMessage(String messageEn);

    void oldPasswordErrorMessage(String messageEn);

    void passwordUpdated(ResponseBody responseBody);

    void processLogout();
}
