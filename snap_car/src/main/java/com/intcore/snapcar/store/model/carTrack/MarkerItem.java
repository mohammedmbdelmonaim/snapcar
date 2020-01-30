package com.intcore.snapcar.store.model.carTrack;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.constant.FilterType;

public class MarkerItem {

    @SerializedName("id")
    private Integer id;
    @SerializedName("user_id")
    private Integer userId;

    private Double latitude;
    private Double longitude;

    private FilterType type;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public LatLng getLocation(){
        if (latitude == null || longitude == null)
            return null;

        return new LatLng(latitude,longitude);
    }
}
