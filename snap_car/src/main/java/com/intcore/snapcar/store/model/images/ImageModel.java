package com.intcore.snapcar.store.model.images;

public class ImageModel {

    private int id;
    private int carId;
    private String place;
    private int isMain;
    private String image;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;

    ImageModel(int id,
               int carId,
               String place,
               int isMain,
               String image,
               String deletedAt,
               String createdAt,
               String updatedAt) {
        this.id = id;
        this.carId = carId;
        this.place = place;
        this.isMain = isMain;
        this.image = image;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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
