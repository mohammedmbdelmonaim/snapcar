package com.intcore.snapcar.store.model.discount;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.coupon.CouponApiResponse;

import java.util.List;

public class DiscountApiResponse {

    @SerializedName("hotzone_coupons")
    private List<CouponApiResponse> hotZoneApiResponse;

    @SerializedName("other_coupons")
    private List<CouponApiResponse> otherCouponsApiResponse;

    public List<CouponApiResponse> getOtherCouponsApiResponse() {
        return otherCouponsApiResponse;
    }

    public List<CouponApiResponse> getHotZoneApiResponse() {
        return hotZoneApiResponse;
    }
}