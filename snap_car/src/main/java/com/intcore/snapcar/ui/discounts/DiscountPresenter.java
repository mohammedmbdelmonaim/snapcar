package com.intcore.snapcar.ui.discounts;

import com.intcore.snapcar.store.model.coupon.CouponViewModel;
import com.intcore.snapcar.core.scope.FragmentScope;

import java.util.ArrayList;

import javax.inject.Inject;

@FragmentScope
class DiscountPresenter {

    private final DiscountScreen discountScreen;

    @Inject
    DiscountPresenter(DiscountScreen discountScreen) {
        this.discountScreen = discountScreen;
    }

    void onViewCreated(ArrayList<CouponViewModel> couponViewModels) {
        discountScreen.setupRecyclerView();
        discountScreen.updateUi(couponViewModels);
    }

    public void showSuccessMessage(String message) {
        discountScreen.showSuccessMessage(message);
    }
}
