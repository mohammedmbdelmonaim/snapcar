package com.intcore.snapcar.store.model.category;

import com.google.gson.annotations.SerializedName;

public class CategoryApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("name_ar")
    private String nameAr;
    @SerializedName("name_en")
    private String nameEn;

    public int getId() {
        return id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

}