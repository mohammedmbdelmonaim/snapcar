package com.intcore.snapcar.store.model.filterdetail;

import com.google.gson.annotations.SerializedName;

public class FilterDetailApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("long")
    private String longitude;
    @SerializedName("lat")
    private String latitude;
    @SerializedName("image")
    private String image;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getImage() {
        return image;
    }

    public String getLatitude() {
        return latitude;
    }
}