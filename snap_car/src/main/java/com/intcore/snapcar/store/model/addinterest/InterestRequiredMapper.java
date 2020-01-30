package com.intcore.snapcar.store.model.addinterest;

import com.intcore.snapcar.store.model.brands.BrandsMapper;
import com.intcore.snapcar.store.model.carcolor.CarColorMapper;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.importer.ImporterMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class InterestRequiredMapper {

    private final BrandsMapper brandsMapper;
    private final CountryMapper countryMapper;
    private final CarColorMapper carColorMapper;
    private final ImporterMapper importerMapper;

    @Inject
    InterestRequiredMapper(BrandsMapper brandsMapper, CountryMapper countryMapper, CarColorMapper carColorMapper, ImporterMapper importerMapper) {
        this.brandsMapper = brandsMapper;
        this.countryMapper = countryMapper;
        this.carColorMapper = carColorMapper;
        this.importerMapper = importerMapper;
    }

    public InterestRequiredModel toInterestRequiredModel(InterestRequiredApiResponse response) {
        return new InterestRequiredModel(
                response.getMinPrice(),
                response.getMaxPrice(),
                brandsMapper.toBrandsModels(response.getBrandsList()),
                carColorMapper.toCarColorModels(response.getCarColorsApiResponseList()),
                countryMapper.toCountryModels(response.getCountryApiResponseList()),
                importerMapper.toImporterModels(response.getSpecificationsApiResponseList()));
    }

    public InterestRequiredViewModel toInterestRequiredViewModel(InterestRequiredModel model) {
        return new InterestRequiredViewModel(model.getMinPrice(),
                model.getMaxPrice(),
                brandsMapper.toBrandsViewModels(model.getBrandsList()),
                importerMapper.toImporterViewModels(model.getImporterModels()),
                carColorMapper.toCarColorViewModels(model.getCarColorsApiResponseList()),
                countryMapper.toCountryViewModels(model.getCountryApiResponseList()));
    }
}