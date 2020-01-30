package com.intcore.snapcar.ui.hotzone;

import com.intcore.snapcar.store.model.coupon.CouponViewModel;

import java.util.ArrayList;

public interface HotZoneScreen {

    void updateUi(ArrayList<CouponViewModel> couponViewModels);

    void setupRecyclerView();

    void showSuccessMessage(String message);
}
