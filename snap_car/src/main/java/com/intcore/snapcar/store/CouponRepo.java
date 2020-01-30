package com.intcore.snapcar.store;

import com.intcore.snapcar.store.model.discount.DiscountMapper;
import com.intcore.snapcar.store.model.discount.DiscountModel;
import com.intcore.snapcar.core.scope.ActivityScope;

import javax.inject.Inject;

import io.reactivex.Single;

@ActivityScope
public class CouponRepo {

    private final WebServiceStore webServiceStore;
    private final DiscountMapper discountMapper;

    @Inject
    CouponRepo(WebServiceStore webServiceStore, DiscountMapper discountMapper) {
        this.webServiceStore = webServiceStore;
        this.discountMapper = discountMapper;
    }

    public Single<DiscountModel> fetchCoupons(String apiToken) {
        return webServiceStore.fetchCoupons(apiToken)
                .map(discountMapper::toDiscountModel);
    }
}