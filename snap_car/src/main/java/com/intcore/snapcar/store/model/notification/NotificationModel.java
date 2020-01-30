package com.intcore.snapcar.store.model.notification;

import com.intcore.snapcar.store.model.notificationdata.NotificationDataModel;

public class NotificationModel {

    private String id;
    private int notifierId;
    private String readAt;
    private int notifiableId;
    private String createdAt;
    private String headTime;
    private String date;
    private NotificationDataModel dataModel;

    NotificationModel(String id,
                      int notifierId,
                      String readAt,
                      int notifiableId,
                      String createdAt,
                      String headTime,
                      String date,
                      NotificationDataModel dataModel) {
        this.id = id;
        this.readAt = readAt;
        this.headTime = headTime;
        this.createdAt = createdAt;
        this.dataModel = dataModel;
        this.notifierId = notifierId;
        this.date = date;
        this.notifiableId = notifiableId;
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

    public NotificationDataModel getDataModel() {
        return dataModel;
    }
}