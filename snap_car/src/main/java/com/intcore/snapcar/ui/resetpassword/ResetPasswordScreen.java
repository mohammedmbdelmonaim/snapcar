package com.intcore.snapcar.ui.resetpassword;

import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface ResetPasswordScreen extends BaseActivityScreen {

    void showConfirmPasswordErrorMessage(String confirmPasswordErrorMsg);

    void showPasswordErrorMessage(String passwordErrorMsg);

    void onReadyToResetPassword(String phone, String code);

    void setupEditText();
}