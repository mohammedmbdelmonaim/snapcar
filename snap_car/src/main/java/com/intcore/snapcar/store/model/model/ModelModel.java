package com.intcore.snapcar.store.model.model;

import com.intcore.snapcar.store.model.category.CategoryModel;

import java.util.Collections;
import java.util.List;

public class ModelModel {

    private int id;
    private String nameAr;
    private String nameEn;
    private List<CategoryModel> categoryModels;

    ModelModel(int id, String nameAr, String nameEn, List<CategoryModel> categoryModels) {
        this.id = id;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
        this.categoryModels = categoryModels;
    }

    public static ModelModel createDefault() {
        return new ModelModel(0, "Show all", "Show all", Collections.emptyList());
    }

    public int getId() {
        return id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public List<CategoryModel> getCategoryModels() {
        return categoryModels;
    }
}