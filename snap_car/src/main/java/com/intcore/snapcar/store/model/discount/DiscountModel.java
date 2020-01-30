package com.intcore.snapcar.store.model.discount;

import com.intcore.snapcar.store.model.coupon.CouponModel;

import java.util.List;

public class DiscountModel {

    private List<CouponModel> couponModel;

    private List<CouponModel> otherCouponModel;

    DiscountModel(List<CouponModel> couponModel ,List<CouponModel> otherCouponModel) {
        this.otherCouponModel = otherCouponModel;
        this.couponModel = couponModel;
    }

    public List<CouponModel> getCouponModel() {
        return couponModel;
    }

    public List<CouponModel> getOtherCouponModel() {
        return otherCouponModel;
    }
}
