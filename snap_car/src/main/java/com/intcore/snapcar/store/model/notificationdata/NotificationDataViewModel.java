package com.intcore.snapcar.store.model.notificationdata;

import com.intcore.snapcar.store.model.constant.NotificationType;

public class NotificationDataViewModel {

    @NotificationType
    private int type;
    private int carId;
    private String title;
    private String message;
    private String commission;

    NotificationDataViewModel(int carId, int type, String title, String message, String commission) {
        this.type = type;
        this.carId = carId;
        this.title = title;
        this.message = message;
        this.commission = commission;
    }

    @NotificationType
    public int getType() {
        return type;
    }

    public int getCarId() {
        return carId;
    }

    public String getTitle() {
        return title;
    }

    public String getCommission() {
        return commission;
    }

    public String getMessage() {
        return message;
    }
}