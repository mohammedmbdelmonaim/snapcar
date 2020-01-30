package com.intcore.snapcar.store.model.notification;

import com.intcore.snapcar.store.model.notificationdata.NotificationDataViewModel;

public class NotificationViewModel {

    private String id;
    private int notifierId;
    private String readAt;
    private int notifiableId;
    private String createdAt;
    private String headTime;
    private String date;
    private NotificationDataViewModel dataViewModel;

    NotificationViewModel(String id,
                          int notifierId,
                          String readAt,
                          int notifiableId,
                          String createdAt,
                          String headTime,
                          String date,
                          NotificationDataViewModel dataViewModel) {
        this.id = id;
        this.date = date;
        this.readAt = readAt;
        this.headTime = headTime;
        this.createdAt = createdAt;
        this.notifierId = notifierId;
        this.notifiableId = notifiableId;
        this.dataViewModel = dataViewModel;
    }

    public String getId() {
        return id;
    }

    public int getNotifierId() {
        return notifierId;
    }

    public String getReadAt() {
        return readAt;
    }

    public String getHeadTime() {
        return headTime;
    }

    public String getDate() {
        return date;
    }

    public int getNotifiableId() {
        return notifiableId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public NotificationDataViewModel getDataViewModel() {
        return dataViewModel;
    }

}
