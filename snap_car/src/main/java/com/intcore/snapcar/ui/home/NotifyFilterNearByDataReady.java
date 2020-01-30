package com.intcore.snapcar.ui.home;

import com.intcore.snapcar.store.model.filter.FilterApiResponse;

public interface NotifyFilterNearByDataReady {
    void onDataReady(FilterApiResponse viewModelList);
}