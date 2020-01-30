package com.intcore.snapcar.ui.coupon;

import com.intcore.snapcar.store.model.discount.DiscountViewModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface CouponScreen extends BaseActivityScreen {

    void setupViewPager();

    void setupRefreshLayout();

    void updateUi(DiscountViewModel discountViewModel);

    void processLogout();
}
