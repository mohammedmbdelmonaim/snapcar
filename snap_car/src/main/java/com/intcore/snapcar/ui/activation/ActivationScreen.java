package com.intcore.snapcar.ui.activation;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface ActivationScreen extends BaseActivityScreen {

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

    void onUpdatedSuccessfully();

    void registerReciever(BroadcastReceiver receiver, IntentFilter filter);

    void setPinEntrycode(String code);

    void unregisterBroadCast(BroadcastReceiver receiver);
}