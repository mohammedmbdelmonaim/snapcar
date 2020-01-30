package com.intcore.snapcar.ui.discounts;

import com.intcore.snapcar.store.model.coupon.CouponViewModel;

import java.util.ArrayList;

public interface DiscountScreen {

    void updateUi(ArrayList<CouponViewModel> couponViewModels);

    void showSuccessMessage(String message);

    void setupRecyclerView();
}