package com.intcore.snapcar.store.model.ads;

import com.google.gson.annotations.SerializedName;

public class AdsApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("name_ar")
    private String nameAr;
    @SerializedName("name_en")
    private String nameEn;
    @SerializedName("image")
    private String image;
    @SerializedName("activation")
    private int activation;
    @SerializedName("expired_at")
    private String expiredAt;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("start_date")
    private String stareDate;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("url")
    private String url;

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public int getActivation() {
        return activation;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getStareDate() {
        return stareDate;
    }

    public String getEndDate() {
        return endDate;
    }
}