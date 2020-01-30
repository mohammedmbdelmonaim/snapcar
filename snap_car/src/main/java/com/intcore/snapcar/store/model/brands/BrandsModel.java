package com.intcore.snapcar.store.model.brands;

import com.intcore.snapcar.store.model.model.ModelModel;

import java.util.Collections;
import java.util.List;

public class BrandsModel {

    private int id;
    private String nameAr;
    private String nameEn;
    private String image;
    private List<ModelModel> modelModels;

    BrandsModel(int id, String nameAr, String nameEn, String image, List<ModelModel> modelModels) {
        this.id = id;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
        this.image = image;
        this.modelModels = modelModels;
    }

    public static BrandsModel createDefault() {
        return new BrandsModel(0, "Show all", "Show all", "", Collections.emptyList());
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

    public String getImage() {
        return image;
    }

    public List<ModelModel> getModelModels() {
        return modelModels;
    }
}