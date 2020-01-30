package com.intcore.snapcar.store.model.carcolor;

import com.google.gson.annotations.SerializedName;

public class CarColorApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("name_ar")
    private String nameAr;
    @SerializedName("name_en")
    private String nameEn;
    @SerializedName("hex")
    private String hex;

    public int getId() {
        return id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getHex() {
        return hex;
    }
}
