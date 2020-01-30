package com.intcore.snapcar.store.model.carresource;

import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;

import java.util.List;

public class CarResourcesViewModel {

    private final List<BrandsViewModel> brands;
    private final List<CarColorViewModel> colors;
    private final List<ImporterViewModel> specifications;
    private final int hasTrackedCar;
    private final int publishedCars;
    private final int availableCarToAdd;

    public CarResourcesViewModel(List<BrandsViewModel> brands, List<CarColorViewModel> colors,
                                 List<ImporterViewModel> specifications, int hasTrackedCar, int publishedCars, int availableCarToAdd) {
        this.brands = brands;
        this.colors = colors;
        this.specifications = specifications;
        this.hasTrackedCar = hasTrackedCar;
        this.publishedCars = publishedCars;
        this.availableCarToAdd = availableCarToAdd;
    }

    public List<BrandsViewModel> getBrands() {
        return brands;
    }

    public List<CarColorViewModel> getColors() {
        return colors;
    }

    public List<ImporterViewModel> getSpecifications() {
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
