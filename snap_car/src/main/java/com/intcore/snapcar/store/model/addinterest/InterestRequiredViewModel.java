package com.intcore.snapcar.store.model.addinterest;

import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;

import java.util.Collections;
import java.util.List;

public class InterestRequiredViewModel {

    private String minPrice;
    private String maxPrice;
    private List<BrandsViewModel> brandsViewModels;
    private List<ImporterViewModel> importerViewModels;
    private List<CarColorViewModel> colorViewModels;
    private List<CountryViewModel> countryViewModels;

    InterestRequiredViewModel(String minPrice,
                              String maxPrice,
                              List<BrandsViewModel> brandsViewModels,
                              List<ImporterViewModel> importerViewModels,
                              List<CarColorViewModel> colorViewModels,
                              List<CountryViewModel> countryViewModels) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.colorViewModels = colorViewModels;
        this.brandsViewModels = brandsViewModels;
        this.importerViewModels = importerViewModels;
        this.countryViewModels = countryViewModels;
    }

    public static InterestRequiredViewModel createDefault() {
        return new InterestRequiredViewModel(null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public List<BrandsViewModel> getBrandsViewModels() {
        return brandsViewModels;
    }

    public List<ImporterViewModel> getImporterViewModels() {
        return importerViewModels;
    }

    public List<CarColorViewModel> getColorViewModels() {
        return colorViewModels;
    }

    public List<CountryViewModel> getCountryViewModels() {
        return countryViewModels;
    }

}
