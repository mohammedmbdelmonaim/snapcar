package com.intcore.snapcar.ui.paymenactivity;

import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivityPresenter;

import javax.inject.Inject;

public class PaymentPresenter extends BaseActivityPresenter {

    private final PaymentScreen screen;
    private final UserManager userManager;

    @Inject
    PaymentPresenter(PaymentScreen screen,
                     UserManager userManager) {
        super(screen);
        this.screen = screen;
        this.userManager = userManager;
    }


    String getUserApiToken() {
        if (userManager.sessionManager().isSessionActive()) {
            return userManager.getCurrentUser().getApiToken();
        } else {
            return null;
        }
    }
}
