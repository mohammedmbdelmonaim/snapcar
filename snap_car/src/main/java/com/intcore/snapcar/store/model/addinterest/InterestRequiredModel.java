package com.intcore.snapcar.store.model.addinterest;

import com.intcore.snapcar.store.model.brands.BrandsModel;
import com.intcore.snapcar.store.model.carcolor.CarColorModel;
import com.intcore.snapcar.store.model.country.CountryModel;
import com.intcore.snapcar.store.model.importer.ImporterModel;

import java.util.Collections;
import java.util.List;

public class InterestRequiredModel {

    private String minPrice;
    private String maxPrice;
    private List<BrandsModel> brandsList;
    private List<ImporterModel> importerModels;
    private List<CarColorModel> carColorsApiResponseList;
    private List<CountryModel> countryApiResponseList;

    InterestRequiredModel(String minPrice,
                          String maxPrice,
                          List<BrandsModel> brandsList,
                          List<CarColorModel> carColorsApiResponseList,
                          List<CountryModel> countryApiResponseList,
                          List<ImporterModel> importerModels) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.brandsList = brandsList;
        this.importerModels = importerModels;
        this.carColorsApiResponseList = carColorsApiResponseList;
        this.countryApiResponseList = countryApiResponseList;
    }

    public static InterestRequiredModel createDefault() {
        return new InterestRequiredModel(null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public List<BrandsModel> getBrandsList() {
        return brandsList;
    }

    public List<ImporterModel> getImporterModels() {
        return importerModels;
    }

    public List<CarColorModel> getCarColorsApiResponseList() {
        return carColorsApiResponseList;
    }

    public List<CountryModel> getCountryApiResponseList() {
        return countryApiResponseList;
    }
}