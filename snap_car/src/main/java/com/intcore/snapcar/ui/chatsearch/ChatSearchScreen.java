package com.intcore.snapcar.ui.chatsearch;

import com.intcore.snapcar.core.base.BaseActivityScreen;
import com.intcore.snapcar.core.chat.ChatViewModel;

import java.util.List;

public interface ChatSearchScreen extends BaseActivityScreen {

    void updateUi(List<ChatViewModel> chatModels);

    void notifyItemRemoved(int position);

    void onReportSuccessfully();

    void setupRefreshLayout();

    void setupRecyclerView();

    void setupEditText();

    void showNoData();
}