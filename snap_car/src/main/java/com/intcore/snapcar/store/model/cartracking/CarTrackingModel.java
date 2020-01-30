package com.intcore.snapcar.store.model.cartracking;

public class CarTrackingModel {

    private int id;
    private int carId;
    private String oldLat;
    private String oldLong;
    private String latitude;
    private String longitude;

    CarTrackingModel(int id, int carId, String oldLat, String oldLong, String latitude, String longitude) {
        this.id = id;
        this.carId = carId;
        this.oldLat = oldLat;
        this.oldLong = oldLong;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public int getCarId() {
        return carId;
    }

    public String getOldLat() {
        return oldLat;
    }

    public String getOldLong() {
        return oldLong;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

}