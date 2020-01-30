package com.intcore.snapcar.store.model.discount;

import com.intcore.snapcar.store.model.coupon.CouponMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class DiscountMapper {

    private final CouponMapper couponMapper;

    @Inject
    DiscountMapper(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    public DiscountModel toDiscountModel(DiscountApiResponse response) {
        return new DiscountModel(couponMapper.toCouponModels(response.getHotZoneApiResponse()),
                couponMapper.toCouponModels(response.getOtherCouponsApiResponse()));
    }

    public DiscountViewModel toDiscountViewModel(DiscountModel model) {
        return new DiscountViewModel(couponMapper.toCouponsViewModels(model.getCouponModel()),
                couponMapper.toCouponsViewModels(model.getOtherCouponModel()));
    }
}
