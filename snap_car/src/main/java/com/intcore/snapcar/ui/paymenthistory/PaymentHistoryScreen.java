package com.intcore.snapcar.ui.paymenthistory;

import com.intcore.snapcar.store.model.paymenthistory.PaymentHistoryApiResponse;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.util.List;

public interface PaymentHistoryScreen extends BaseActivityScreen {
    void updateUi(List<PaymentHistoryApiResponse> paymentHistoryApiResponse);

    void processLogout();
}
