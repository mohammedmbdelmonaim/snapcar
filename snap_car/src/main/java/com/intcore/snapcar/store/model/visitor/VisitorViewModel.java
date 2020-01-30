package com.intcore.snapcar.store.model.visitor;

import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;

import java.util.List;

public class VisitorViewModel {

    private List<CarViewModel> soldCarViewModel;
    private DefaultUserModel defaultUserModel;
    private List<CarViewModel> carViewModel;

    VisitorViewModel(DefaultUserModel defaultUserModel, List<CarViewModel> carViewModel, List<CarViewModel> soldCarViewModel) {
        this.defaultUserModel = defaultUserModel;
        this.soldCarViewModel = soldCarViewModel;
        this.carViewModel = carViewModel;
    }

    public DefaultUserModel getDefaultUserModel() {
        return defaultUserModel;
    }

    public List<CarViewModel> getSoldCarViewModel() {
        return soldCarViewModel;
    }

    public List<CarViewModel> getCarViewModel() {
        return carViewModel;
    }
}