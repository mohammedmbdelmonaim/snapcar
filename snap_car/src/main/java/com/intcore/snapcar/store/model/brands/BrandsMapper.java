package com.intcore.snapcar.store.model.brands;

import com.intcore.snapcar.store.model.model.ModelMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@ApplicationScope
public class BrandsMapper {

    private final ModelMapper modelMapper;

    @Inject
    BrandsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public BrandsModel toBrandsModel(BrandsApiResponse response) {
        if (response == null) return null;
        return new BrandsModel(response.getId(),
                response.getNameAr(),
                response.getNameEn(),
                response.getImage(),
                modelMapper.toModelsModels(response.getModelApiResponses()));
    }

    public List<BrandsModel> toBrandsModels(List<BrandsApiResponse> responses) {
        if (responses == null) return null;
        List<BrandsModel> models = new ArrayList<>();
        for (BrandsApiResponse response : responses) {
            models.add(toBrandsModel(response));
        }
        return models;
    }

    public BrandsViewModel toBrandsViewModel(BrandsModel model) {
        if (model == null) return null;
        String name = model.getNameAr();
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
            name = model.getNameEn();
        }
        return new BrandsViewModel(
                model.getId(),
                name,
                model.getImage(),
                modelMapper.toModelViewModels(model.getModelModels()));
    }

    public List<BrandsViewModel> toBrandsViewModels(List<BrandsModel> models) {
        if (models == null) return null;
        List<BrandsViewModel> viewModels = new ArrayList<>();
        for (BrandsModel response : models) {
            viewModels.add(toBrandsViewModel(response));
        }
        return viewModels;
    }
}