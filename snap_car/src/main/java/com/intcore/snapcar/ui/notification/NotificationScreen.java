package com.intcore.snapcar.ui.notification;

import com.intcore.snapcar.store.model.notification.NotificationViewModel;

import java.util.List;

public interface NotificationScreen {

    void updateUi(List<List<NotificationViewModel>> notificationModels);

    void showWarningMessage(String warningMsg);

    void showErrorMessage(String errorMsg);

    void shouldNavigateToProfile();

    void hideLoadingAnimation();

    void showLoadingAnimation();

    void setupRefreshLayout();

    void setupRecyclerView();

    void shouldNavigateToHome();
}