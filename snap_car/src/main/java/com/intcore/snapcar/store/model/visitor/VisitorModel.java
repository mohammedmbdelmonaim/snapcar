package com.intcore.snapcar.store.model.visitor;

import com.intcore.snapcar.store.model.car.CarModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;

import java.util.List;

public class VisitorModel {

    private DefaultUserModel defaultUserModel;
    private List<CarModel> soldCarList;
    private List<CarModel> carModels;

    VisitorModel(DefaultUserModel defaultUserModel, List<CarModel> carModels, List<CarModel> soldCarList) {
        this.defaultUserModel = defaultUserModel;
        this.soldCarList = soldCarList;
        this.carModels = carModels;
    }

    public DefaultUserModel getDefaultUserModel() {
        return defaultUserModel;
    }

    public List<CarModel> getSoldCarList() {
        return soldCarList;
    }

    public List<CarModel> getCarModels() {
        return carModels;
    }
}