package com.intcore.snapcar.store.model.filterdetail;

import com.intcore.snapcar.store.model.constant.FilterType;

public class FilterDetailModel {

    private int id;
    @FilterType
    private int type;
    private String title;
    private String image;
    private String latitude;
    private String longitude;
    private String description;

    FilterDetailModel(int type,
                      int id,
                      String title,
                      String image,
                      String description,
                      String longitude,
                      String latitude) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    @FilterType
    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}