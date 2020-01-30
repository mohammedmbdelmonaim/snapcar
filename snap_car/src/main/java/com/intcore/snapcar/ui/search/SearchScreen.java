package com.intcore.snapcar.ui.search;

import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.util.List;

public interface SearchScreen extends BaseActivityScreen {

    void updateUi(List<CarViewModel> models);

    void setupRecyclerView();

    void appendSearchResultToBottom(List<CarViewModel> carModels);

    void updateFeatureCars(List<CarViewModel> carViewModels);

    void setupFeatureRecyclerView();

    void showPopUpInterestMessage(SearchRequestModel value);

    void updateAdapter();

    void processLogout();
}