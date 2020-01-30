package com.intcore.snapcar.ui.chat;

import androidx.recyclerview.widget.DiffUtil;

import com.intcore.snapcar.core.chat.model.PlaceDTO;

public interface ChatScreen {

    void shouldNavigateToChat(int chatId, int messageId, String firstName, PlaceDTO place);

    void updateUi(DiffUtil.DiffResult diffResult);

    void showWarningMessage(String warningMsg);

    void showErrorMessage(String errorMsg);

    void showSuccessMessage(String msg);

    void showNetworkDisconnected();

    void showNetworkConnecting();

    void showNetworkConnected();

    void setupRecyclerView();

    void setupRefreshLayout();

    void showLoadingAnimation();

    void hideLoadingAnimation();

    void onReportSuccessfully();

    void onChatDeleted();
}