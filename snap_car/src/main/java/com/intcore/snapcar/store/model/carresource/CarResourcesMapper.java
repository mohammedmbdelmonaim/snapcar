package com.intcore.snapcar.store.model.carresource;

import com.intcore.snapcar.store.model.brands.BrandsMapper;
import com.intcore.snapcar.store.model.carcolor.CarColorMapper;
import com.intcore.snapcar.store.model.importer.ImporterMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class CarResourcesMapper {
    private final BrandsMapper brandsMapper;
    private final CarColorMapper carColorMapper;
    private final ImporterMapper importerMapper;

    @Inject
    CarResourcesMapper(BrandsMapper brandsMapper, CarColorMapper carColorMapper, ImporterMapper importerMapper, BrandsMapper brandsMapper1, CarColorMapper carColorMapper1, ImporterMapper importerMapper1) {
        this.brandsMapper = brandsMapper1;
        this.carColorMapper = carColorMapper1;
        this.importerMapper = importerMapper1;
    }


    public CarResourcesModel toModel(CarResourcesApiResponse carResourcesApiResponse) {
        if (carResourcesApiResponse == null) return null;
        return new CarResourcesModel(brandsMapper.toBrandsModels(carResourcesApiResponse.getBrands()),carColorMapper.toCarColorModels(
                carResourcesApiResponse.getColors()),importerMapper.toImporterModels(carResourcesApiResponse.getSpecifications()),
                carResourcesApiResponse.getHasTrackedCar(),
                carResourcesApiResponse.getPublishedCars(),
                carResourcesApiResponse.getAvailableCarToAdd());
    }

    public CarResourcesViewModel toViewModel(CarResourcesModel carResourcesModel) {
        if (carResourcesModel == null) return null;
        return new CarResourcesViewModel(brandsMapper.toBrandsViewModels(carResourcesModel.getBrands()),carColorMapper.toCarColorViewModels(
                carResourcesModel.getColors()),importerMapper.toImporterViewModels(carResourcesModel.getSpecifications()),
                carResourcesModel.getHasTrackedCar(),
                carResourcesModel.getPublishedCars(),
                carResourcesModel.getAvailableCarToAdd());
    }
}
