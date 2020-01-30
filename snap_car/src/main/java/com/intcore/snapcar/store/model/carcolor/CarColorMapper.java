package com.intcore.snapcar.store.model.carcolor;

import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@ApplicationScope
public class CarColorMapper {

    @Inject
    CarColorMapper() {
    }

    public CarColorModel toCarColorModel(CarColorApiResponse response) {
        if (response == null) return null;
        return new CarColorModel(response.getId(),
                response.getHex(),
                response.getNameAr(),
                response.getNameEn());
    }

    public List<CarColorModel> toCarColorModels(List<CarColorApiResponse> responses) {
        if (responses == null) return null;
        List<CarColorModel> models = new ArrayList<>();
        for (CarColorApiResponse response : responses) {
            models.add(toCarColorModel(response));
        }
        return models;
    }

    public CarColorViewModel toCarColorViewModel(CarColorModel model) {
        if (model == null) return null;
        String name = model.getNameAr();
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
            name = model.getNameEn();
        }
        return new CarColorViewModel(model.getId(),
                model.getHex(),
                name);
    }

    public List<CarColorViewModel> toCarColorViewModels(List<CarColorModel> models) {
        if (models == null) return null;
        List<CarColorViewModel> viewModels = new ArrayList<>();
        for (CarColorModel model : models) {
            viewModels.add(toCarColorViewModel(model));
        }
        return viewModels;
    }
}
