package com.intcore.snapcar.store.model.explore;

import com.intcore.snapcar.store.model.car.CarViewModel;

import static com.intcore.snapcar.store.model.explore.ExploreItemType.CAR;

public class CarExploreItem extends ExploreItem {

    private final CarViewModel carViewModel;

    public CarExploreItem(CarViewModel carViewModel) {
        this.carViewModel = carViewModel;
    }

    public CarViewModel getCarItem() {
        return carViewModel;
    }

    @Override
    public int getType() {
        return CAR;
    }
}