package com.intcore.snapcar.store.model.brands;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.model.ModelApiResponse;

import java.util.List;

public class BrandsApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("name_ar")
    private String nameAr;
    @SerializedName("name_en")
    private String nameEn;
    @SerializedName("image")
    private String image;
    @SerializedName("car_models")
    private List<ModelApiResponse> modelApiResponses;

    public String getImage() {
        return image;
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

    public List<ModelApiResponse> getModelApiResponses() {
        return modelApiResponses;
    }
}