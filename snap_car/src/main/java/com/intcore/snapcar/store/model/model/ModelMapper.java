package com.intcore.snapcar.store.model.model;

import com.intcore.snapcar.store.model.category.CategoryMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@ApplicationScope
public class ModelMapper {

    private final CategoryMapper categoryMapper;

    @Inject
    ModelMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public ModelModel toModelModel(ModelApiResponse response) {
        if (response == null) return null;
        return new ModelModel(response.getId(),
                response.getNameAr(),
                response.getNameEn(),
                categoryMapper.toCategoryModels(response.getCategoryApiResponses()));
    }

    public List<ModelModel> toModelsModels(List<ModelApiResponse> responses) {
        if (responses == null) return null;
        List<ModelModel> models = new ArrayList<>();
        for (ModelApiResponse response : responses) {
            models.add(toModelModel(response));
        }
        return models;
    }

    public ModelViewModel toModelViewModel(ModelModel model) {
        if (model == null) return null;
        String name = model.getNameAr();
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
            name = model.getNameEn();
        }
        return new ModelViewModel(model.getId(),
                name,
                categoryMapper.toCategoryViewModels(model.getCategoryModels()));
    }

    public List<ModelViewModel> toModelViewModels(List<ModelModel> models) {
        if (models == null) return null;
        List<ModelViewModel> viewModels = new ArrayList<>();
        for (ModelModel model : models) {
            viewModels.add(toModelViewModel(model));
        }
        return viewModels;
    }

}