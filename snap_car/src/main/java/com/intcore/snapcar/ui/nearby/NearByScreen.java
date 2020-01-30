package com.intcore.snapcar.ui.nearby;

import com.intcore.snapcar.store.model.explore.ExploreItem;

import java.util.List;

public interface NearByScreen {

    void updateUi(List<ExploreItem> paginateexploreItemsRelay);

    void setupRecyclerView();

    void setupRefreshLayout();

    void showLoadingAnimation();

    void hideLoadingAnimation();

    void showErrorMessage(String errorMsg);
    void showSuccessMessage(String msg);

    void showNoDataIndicator();

    void processLogout();

    void notifyAdapter(int counter , int size);

    void appendData(List<ExploreItem> exploreItems);
}