package com.intcore.snapcar.store.model.category;

import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@ApplicationScope
public class CategoryMapper {


    @Inject
    CategoryMapper() {
    }

    public CategoryModel toCategoryModel(CategoryApiResponse response) {
        if (response == null) return null;
        return new CategoryModel(response.getId(),
                response.getNameAr(),
                response.getNameEn());
    }

    public List<CategoryModel> toCategoryModels(List<CategoryApiResponse> responses) {
        if (responses == null) return null;
        List<CategoryModel> models = new ArrayList<>();
        for (CategoryApiResponse response : responses) {
            models.add(toCategoryModel(response));
        }
        return models;
    }

    public CategoryViewModel toCategoryViewModel(CategoryModel model) {
        if (model == null) return null;
        String name = model.getNameAr();
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
            name = model.getNameEn();
        }
        return new CategoryViewModel(model.getId(),
                name);
    }

    public List<CategoryViewModel> toCategoryViewModels(List<CategoryModel> models) {
        if (models == null) return null;
        List<CategoryViewModel> viewModels = new ArrayList<>();
        for (CategoryModel model : models) {
            viewModels.add(toCategoryViewModel(model));
        }
        return viewModels;
    }
}