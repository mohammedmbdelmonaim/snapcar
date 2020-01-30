package com.intcore.snapcar.store.model.carresource;

import com.intcore.snapcar.store.model.brands.BrandsModel;
import com.intcore.snapcar.store.model.carcolor.CarColorModel;
import com.intcore.snapcar.store.model.importer.ImporterModel;

import java.util.List;

public class CarResourcesModel {

    private final List<BrandsModel> brands;
    private final List<CarColorModel> colors;
    private final List<ImporterModel> specifications;
    private final int hasTrackedCar;
    private final int publishedCars;
    private final int availableCarToAdd;

    public CarResourcesModel(List<BrandsModel> brands, List<CarColorModel> colors, List<ImporterModel> specifications, int hasTrackedCar, int publishedCars, int availableCarToAdd) {
        this.brands = brands;
        this.colors = colors;
        this.specifications = specifications;
        this.hasTrackedCar = hasTrackedCar;
        this.publishedCars = publishedCars;
        this.availableCarToAdd = availableCarToAdd;
    }

    public List<BrandsModel> getBrands() {
        return brands;
    }

    public List<CarColorModel> getColors() {
        return colors;
    }

    public List<ImporterModel> getSpecifications() {
        return specifications;
    }

    public int getHasTrackedCar() {
        return hasTrackedCar;
    }

    public int getPublishedCars() {
        return publishedCars;
    }

    public int getAvailableCarToAdd() {
        return availableCarToAdd;
    }
}
