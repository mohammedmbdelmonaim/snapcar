package com.intcore.snapcar.store.model.carTrack;

import com.google.android.gms.maps.model.LatLng;

public class TrackModel {

    private int carId;
    private String latitude;
    private String longitude;
    private String oldLatitude;
    private String oldLongitude;

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOldLatitude() {
        return oldLatitude;
    }

    public void setOldLatitude(String oldLatitude) {
        this.oldLatitude = oldLatitude;
    }

    public String getOldLongitude() {
        return oldLongitude;
    }

    public void setOldLongitude(String oldLongitude) {
        this.oldLongitude = oldLongitude;
    }

    public LatLng getLocation(){
        if (latitude == null || longitude == null || latitude.isEmpty() || longitude.isEmpty())
            return null;

        return new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }

    public LatLng getOldLocation(){
        if (oldLatitude == null || oldLongitude == null || oldLatitude.isEmpty() || oldLongitude.isEmpty())
            return null;

        return new LatLng(Double.parseDouble(oldLatitude),Double.parseDouble(oldLongitude));
    }
}
