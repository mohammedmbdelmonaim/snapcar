package com.intcore.snapcar.store.model.ads;

public class AdsModel {

    private int id;
    private String url;
    private String image;
    private String nameAr;
    private String nameEn;
    private int activation;
    private String endDate;
    private String expiredAt;
    private String stareDate;
    private String createdAt;

    AdsModel(int id,
             String url,
             String image,
             String nameAr,
             String nameEn,
             int activation,
             String endDate,
             String expiredAt,
             String stareDate,
             String createdAt) {
        this.id = id;
        this.url = url;
        this.image = image;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.stareDate = stareDate;
        this.activation = activation;
    }

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

    public String getEndDate() {
        return endDate;
    }

    public int getActivation() {
        return activation;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getStareDate() {
        return stareDate;
    }
}