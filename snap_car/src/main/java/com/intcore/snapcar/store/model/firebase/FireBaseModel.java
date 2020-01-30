package com.intcore.snapcar.store.model.firebase;

public class FireBaseModel {

    public int carId;
    public int userId;
    public String latitude;
    public String longitude;
    public String oldLongitude;
    public String oldLatitude;

    public FireBaseModel() {
    }

    public FireBaseModel(int carId, int userId, String longitude, String latitude, String oldLatitude, String oldLongitude) {
        this.carId = carId;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.oldLatitude = oldLatitude;
        this.oldLongitude = oldLongitude;
    }

    public int getCarId() {
        return carId;
    }

    public int getUserId() {
        return userId;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getOldLatitude() {
        return oldLatitude;
    }

    public String getOldLongitude() {
        return oldLongitude;
    }
}