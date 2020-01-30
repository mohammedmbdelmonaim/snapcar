package com.intcore.snapcar.store.model.discount;

import com.intcore.snapcar.store.model.coupon.CouponViewModel;

import java.util.ArrayList;
import java.util.List;

public class DiscountViewModel {

    private List<CouponViewModel> couponViewModel;

    private List<CouponViewModel> otherCouponViewModel;

    DiscountViewModel(List<CouponViewModel> couponViewModel, List<CouponViewModel> otherCouponViewModel) {
        this.otherCouponViewModel = otherCouponViewModel;
        this.couponViewModel = couponViewModel;
    }

    public ArrayList<CouponViewModel> getCouponViewModel() {
        return (ArrayList<CouponViewModel>) couponViewModel;
    }

    public ArrayList<CouponViewModel> getOtherCouponViewModel() {
        return (ArrayList<CouponViewModel>) otherCouponViewModel;
    }
}