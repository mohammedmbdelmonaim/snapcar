package com.intcore.snapcar.ui.hotzone;

import com.intcore.snapcar.store.model.coupon.CouponViewModel;
import com.intcore.snapcar.core.scope.FragmentScope;

import java.util.ArrayList;

import javax.inject.Inject;

@FragmentScope
class HotZonePresenter {

    private final HotZoneScreen hotZoneScreen;

    @Inject
    HotZonePresenter(HotZoneScreen hotZoneScreen) {
        this.hotZoneScreen = hotZoneScreen;
    }

    void onViewCreated(ArrayList<CouponViewModel> couponViewModels) {
        hotZoneScreen.setupRecyclerView();
        hotZoneScreen.updateUi(couponViewModels);
    }

    void showSuccessMessage(String text_copied) {
        hotZoneScreen.showSuccessMessage(text_copied);
    }
}
