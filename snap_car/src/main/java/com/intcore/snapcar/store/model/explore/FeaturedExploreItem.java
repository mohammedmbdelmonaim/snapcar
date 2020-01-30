package com.intcore.snapcar.store.model.explore;

import com.intcore.snapcar.store.model.car.CarViewModel;

import java.util.List;

public class FeaturedExploreItem extends ExploreItem {

    private final List<CarViewModel> carViewModels;

    FeaturedExploreItem(List<CarViewModel> carViewModels) {
        this.carViewModels = carViewModels;
    }

    public List<CarViewModel> getFeatureList() {
        return carViewModels;
    }

    @Override
    public int getType() {
        return ExploreItemType.FEATURE_LIST;
    }
}