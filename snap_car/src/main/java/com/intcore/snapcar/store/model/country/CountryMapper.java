package com.intcore.snapcar.store.model.country;

import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@ApplicationScope
public class CountryMapper {


    @Inject
    CountryMapper() {
    }

    public CountryModel toCountryModel(CountryDataApiResponse.CountryApiResponse response) {
        if (response == null) return null;
        return new CountryModel(
                response.getId(),
                response.getNameAr(),
                response.getImage(),
                response.getNameEn(),
                response.getPhoneCode(),
                response.getPhoneRegex(),
                toCityModels(response.getCountryApiResponses()));
    }

    public CountryModel toCuntryModelWithoutCity(CountryDataApiResponse.CountryApiResponse response) {
        if (response == null) return null;
        return new CountryModel(
                response.getId(),
                response.getNameAr(),
                response.getImage(),
                response.getNameEn(),
                response.getPhoneCode(),
                response.getPhoneRegex());
    }

    public CountryModel toCityModel(CountryDataApiResponse.CountryApiResponse response) {
        if (response == null) return null;
        return new CountryModel(
                response.getId(),
                response.getNameAr(),
                response.getImage(),
                response.getNameEn(),
                response.getPhoneCode(),
                response.getPhoneRegex());
    }

    public CountryViewModel toCountryViewModel(CountryModel model) {
        if (model == null) return null;
        String name = model.getNameAr();
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
            name = model.getNameEn();
        }
        return new CountryViewModel(
                model.getId(),
                model.getPhoneCode(),
                model.getImage(),
                name,
                model.getPhoneRegex(),
                toCityViewModels(model.getCountryModels()));
    }

    public CountryViewModel toCityViewModel(CountryModel model) {
        if (model == null) return null;
        String name = model.getNameAr();
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
            name = model.getNameEn();
        }
        return new CountryViewModel(
                model.getId(),
                model.getPhoneCode(),
                model.getImage(),
                name,
                model.getPhoneRegex());
    }

    public List<CountryModel> toCountryModels(List<CountryDataApiResponse.CountryApiResponse> responses) {
        if (responses == null) return null;
        List<CountryModel> models = new ArrayList<>();
        for (CountryDataApiResponse.CountryApiResponse response : responses) {
            models.add(toCountryModel(response));
        }
        return models;
    }

    public List<CountryViewModel> toCountryViewModels(List<CountryModel> models) {
        if (models == null) return null;
        List<CountryViewModel> viewModels = new ArrayList<>();
        for (CountryModel model : models) {
            viewModels.add(toCountryViewModel(model));
        }
        return viewModels;
    }

    public List<CountryModel> toCityModels(List<CountryDataApiResponse.CountryApiResponse> responses) {
        if (responses == null) return null;
        List<CountryModel> models = new ArrayList<>();
        for (CountryDataApiResponse.CountryApiResponse response : responses) {
            models.add(toCityModel(response));
        }
        return models;
    }

    public List<CountryViewModel> toCityViewModels(List<CountryModel> models) {
        if (models == null) return null;
        List<CountryViewModel> viewModels = new ArrayList<>();
        for (CountryModel model : models) {
            viewModels.add(toCityViewModel(model));
        }
        return viewModels;
    }
}