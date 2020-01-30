package com.intcore.snapcar.ui.myinterstes;

import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.util.List;

public interface MyInterestScreen extends BaseActivityScreen {
    void setInterestList(List<CarViewModel> carModels);

    void appendInterestListToBottom(List<CarViewModel> carModels);

    void setupRecyclerView();

    void removeInterestItem(int interestItemPosition);

    void setupRefreshLayout();

    void processLogout();
}
