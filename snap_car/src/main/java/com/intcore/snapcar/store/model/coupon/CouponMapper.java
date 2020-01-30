package com.intcore.snapcar.store.model.coupon;

import com.intcore.snapcar.store.model.hotzone.HotZoneMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@ApplicationScope
public class CouponMapper {

    private final HotZoneMapper hotZoneMapper;

    @Inject
    CouponMapper(HotZoneMapper hotZoneMapper) {
        this.hotZoneMapper = hotZoneMapper;
    }

    public CouponModel toCouponModel(CouponApiResponse response) {
        if (response == null) return null;
        return new CouponModel(response.getId(),
                response.getUses(),
                response.getNameAr(),
                response.getNameEn(),
                response.getHotZoneId(),
                response.getCoupon(),
                response.getAmount(),
                response.getActivation(),
                response.getExpireAt(),
                response.getDeletedAt(),
                response.getCreatedAt(),
                response.getUpdatedAt(),
                response.getDescriptionAr(),
                response.getDescriptionEn(),
                response.getIsProvidedByHotZone(),
                hotZoneMapper.toHotZoneModel(response.getHotZone()));
    }

    public CouponViewModel toCouponViewModel(CouponModel model) {
        if (model == null) return null;
        return new CouponViewModel(model.getId(),
                model.getUses(),
                model.getNameAr(),
                model.getNameEn(),
                model.getHotZoneId(),
                model.getCoupon(),
                model.getAmount(),
                model.getActivation(),
                model.getExpireAt(),
                model.getDeletedAt(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDescriptionAr(),
                model.getDescriptionEn(),
                model.getIsProvidedByHotZone(),
                hotZoneMapper.toHotZoneViewModel(model.getHotZoneModel()));
    }

    public List<CouponModel> toCouponModels(List<CouponApiResponse> responses) {
        List<CouponModel> models = new ArrayList<>();
        for (CouponApiResponse response : responses) {
            models.add(toCouponModel(response));
        }
        return models;
    }

    public List<CouponViewModel> toCouponsViewModels(List<CouponModel> models) {
        List<CouponViewModel> viewModels = new ArrayList<>();
        for (CouponModel model : models) {
            viewModels.add(toCouponViewModel(model));
        }
        return viewModels;
    }
}