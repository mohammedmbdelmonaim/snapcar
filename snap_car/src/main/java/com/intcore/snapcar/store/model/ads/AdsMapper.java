package com.intcore.snapcar.store.model.ads;

import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@ApplicationScope
public class AdsMapper {

    @Inject
    AdsMapper() {

    }

    public AdsModel toAdsModel(AdsApiResponse response) {
        return new AdsModel(response.getId(),
                response.getUrl(),
                response.getImage(),
                response.getNameAr(),
                response.getNameEn(),
                response.getActivation(),
                response.getEndDate(),
                response.getExpiredAt(),
                response.getStareDate(),
                response.getCreatedAt());
    }

    public AdsViewModel toAdsViewModel(AdsModel model) {
        return new AdsViewModel(model.getId(),
                model.getUrl(),
                model.getNameEn(),
                model.getImage(),
                model.getEndDate(),
                model.getExpiredAt(),
                model.getStareDate(),
                model.getCreatedAt());
    }

    public List<AdsModel> toAdsModels(List<AdsApiResponse> responses) {
        List<AdsModel> models = new ArrayList<>();
        for (AdsApiResponse response : responses) {
            models.add(toAdsModel(response));
        }
        return models;
    }

    public List<AdsViewModel> toAdsViewModels(List<AdsModel> models) {
        List<AdsViewModel> viewModels = new ArrayList<>();
        for (AdsModel model : models) {
            viewModels.add(toAdsViewModel(model));
        }
        return viewModels;
    }
}