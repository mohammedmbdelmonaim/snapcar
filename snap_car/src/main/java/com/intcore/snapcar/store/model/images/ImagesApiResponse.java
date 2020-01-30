package com.intcore.snapcar.store.model.images;

import com.google.gson.annotations.SerializedName;

public class ImagesApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("car_id")
    private int carId;
    @SerializedName("image")
    private String image;
    @SerializedName("place")
    private String place;
    @SerializedName("is_main")
    private int isMain;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() {
        return id;
    }

    public int getCarId() {
        return carId;
    }

    public String getImage() {
        return image;
    }

    public String getPlace() {
        return place;
    }

    public int getIsMain() {
        return isMain;
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
}