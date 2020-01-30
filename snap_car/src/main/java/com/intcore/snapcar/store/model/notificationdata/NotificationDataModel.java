package com.intcore.snapcar.store.model.notificationdata;

public class NotificationDataModel {

    private int type;
    private int carId;
    private String titleEn;
    private String titleAr;
    private String messageEn;
    private String messageAr;
    private String commision;

    NotificationDataModel(int carId, int type, String titleEn, String titleAr, String messageEn, String messageAr, String commision) {
        this.type = type;
        this.carId = carId;
        this.titleEn = titleEn;
        this.titleAr = titleAr;
        this.messageEn = messageEn;
        this.messageAr = messageAr;
        this.commision = commision;
    }

    public int getCarId() {
        return carId;
    }

    public int getType() {
        return type;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public String getMessageAr() {
        return messageAr;
    }

    public String getCommision() {
        return commision;
    }
}