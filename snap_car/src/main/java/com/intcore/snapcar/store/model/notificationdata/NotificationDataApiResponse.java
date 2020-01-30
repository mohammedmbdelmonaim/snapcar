package com.intcore.snapcar.store.model.notificationdata;

import com.google.gson.annotations.SerializedName;

public class NotificationDataApiResponse {
    
    @SerializedName("title_en")
    private String titleEn;
    @SerializedName("message_en")
    private String messageEn;
    @SerializedName("title_ar")
    private String titleAr;
    @SerializedName("message_ar")
    private String messageAr;
    @SerializedName("type")
    private int type;
    @SerializedName("car_id")
    private int carId;
    @SerializedName("commission")
    private String commision;

    public String getTitleEn() {
        return titleEn;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public String getMessageAr() {
        return messageAr;
    }

    public String getCommision() {
        return commision;
    }

    public int getType() {
        return type;
    }

    public int getCarId() {
        return carId;
    }
}