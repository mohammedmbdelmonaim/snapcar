package com.intcore.snapcar.store.model.model;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.category.CategoryApiResponse;

import java.util.List;

public class ModelApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("name_ar")
    private String nameAr;
    @SerializedName("name_en")
    private String nameEn;
    @SerializedName("category")
    private List<CategoryApiResponse> categoryApiResponses;

    public int getId() {
        return id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public List<CategoryApiResponse> getCategoryApiResponses() {
        return categoryApiResponses;
    }
}