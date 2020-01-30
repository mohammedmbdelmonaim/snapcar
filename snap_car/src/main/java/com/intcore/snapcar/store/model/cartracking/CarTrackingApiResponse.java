package com.intcore.snapcar.store.model.cartracking;

import com.google.gson.annotations.SerializedName;

public class CarTrackingApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("car_id")
    private int carId;
    @SerializedName("city_id")
    private int cityId;
    @SerializedName("old_longitude")
    private String oldLong;
    @SerializedName("old_latitude")
    private String oldLat;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("latitude")
    private String latitude;

    public int getId() {
        return id;
    }

    public int getCarId() {
        return carId;
    }

    public int getCityId() {
        return cityId;
    }

    public String getOldLong() {
        return oldLong;
    }

    public String getOldLat() {
        return oldLat;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}