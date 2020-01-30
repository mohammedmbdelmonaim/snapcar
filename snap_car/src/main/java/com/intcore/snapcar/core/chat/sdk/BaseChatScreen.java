package com.intcore.snapcar.core.chat.sdk;

import androidx.recyclerview.widget.DiffUtil;

import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface BaseChatScreen extends BaseActivityScreen {

    void setupEditText();

    void setupRecyclerView();

    void enableInputs();

    void disableInputs();

    void showNetworkConnected();

    void showNetworkConnecting();

    void showNetworkDisconnected();

    void closeChatScreen();

    void updateUi(DiffUtil.DiffResult result);

    void clearInputs();
}
