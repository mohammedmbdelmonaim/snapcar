package com.intcore.snapcar.store.model.ads;

public class AdsViewModel {

    private int id;
    private String url;
    private String name;
    private String image;
    private String endDate;
    private String expiredAt;
    private String stareDate;
    private String createdAt;

    AdsViewModel(int id,
                 String url,
                 String name,
                 String image,
                 String endDate,
                 String expiredAt,
                 String stareDate,
                 String createdAt) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.image = image;
        this.endDate = endDate;
        this.expiredAt = expiredAt;
        this.createdAt = createdAt;
        this.stareDate = stareDate;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public String getStareDate() {
        return stareDate;
    }
}