package com.intcore.snapcar.ui.home;

import com.intcore.snapcar.store.model.explore.ExploreItem;

import java.util.List;

public interface NotifyFilterExploreDataReady {
    void onDataReady(List<ExploreItem> exploreItems);
}