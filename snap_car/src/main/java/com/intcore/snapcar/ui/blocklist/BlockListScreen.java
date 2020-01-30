package com.intcore.snapcar.ui.blocklist;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.util.List;

public interface BlockListScreen extends BaseActivityScreen {

    void appendBlockListToBottom(List<DefaultUserModel> defaultUserModels);

    void setBlockList(List<DefaultUserModel> defaultUserModels);

    void removeUnBlockedItem(int blockItemPosition);

    void setupRefreshLayout();

    void setupRecyclerView();

    void processLogout();
}