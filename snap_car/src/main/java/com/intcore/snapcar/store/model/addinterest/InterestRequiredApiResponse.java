package com.intcore.snapcar.store.model.addinterest;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.brands.BrandsApiResponse;
import com.intcore.snapcar.store.model.carcolor.CarColorApiResponse;
import com.intcore.snapcar.store.model.country.CountryDataApiResponse;
import com.intcore.snapcar.store.model.importer.ImporterApiResponse;

import java.util.List;

public class InterestRequiredApiResponse {

    @SerializedName("brands")
    private List<BrandsApiResponse> brandsList;
    @SerializedName("car_colors")
    private List<CarColorApiResponse> carColorsApiResponseList;
    @SerializedName("specifications")
    private List<ImporterApiResponse> specificationsApiResponseList;
    @SerializedName("countries")
    private List<CountryDataApiResponse.CountryApiResponse> countryApiResponseList;
    @SerializedName("max_price")
    private String maxPrice;
    @SerializedName("min_price")
    private String minPrice;

    public String getMinPrice() {
        return minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public List<BrandsApiResponse> getBrandsList() {
        return brandsList;
    }

    public List<CarColorApiResponse> getCarColorsApiResponseList() {
        return carColorsApiResponseList;
    }

    public List<ImporterApiResponse> getSpecificationsApiResponseList() {
        return specificationsApiResponseList;
    }

    public List<CountryDataApiResponse.CountryApiResponse> getCountryApiResponseList() {
        return countryApiResponseList;
    }

}