package com.intcore.snapcar.ui.confirmcommision;

import com.intcore.snapcar.core.base.BaseActivityScreen;

import okhttp3.ResponseBody;

public interface ConfirmCommissionScreen extends BaseActivityScreen {
    void processLogout();

    void paymentSkiped(ResponseBody responseBody);
}
