package com.intcore.snapcar.ui.editphoneactivition;

import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface EditPhoneActivationScreen extends BaseActivityScreen {

    void setupEditText();

    void shouldResetPassword();

    void shouldNavigateToPhaseTwo();

    void setActivationError(Boolean valid);

    void updateRetryCounter(String counter);

    void setPinEntryEnabled(boolean enable);

    void updatePhoneField(String phoneNumber);

    void setDoneButtonEnabled(Boolean enable);

    void setRetryButtonEnabled(boolean enable);

    void setCounterVisibility(boolean visible);

    void onResendCodeRequested(Integer value, String value1);

    void onActivationReady(int operationType, String key, String code);

    void finishActivity();

    void onUpdatedSuccessfully();

    void processLogout();
}