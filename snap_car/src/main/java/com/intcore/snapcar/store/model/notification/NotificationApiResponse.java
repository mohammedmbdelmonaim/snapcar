package com.intcore.snapcar.store.model.notification;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.notificationdata.NotificationDataApiResponse;

public class NotificationApiResponse {

    @SerializedName("id")
    private String id;
    @SerializedName("notifier_id")
    private int notifierId;
    @SerializedName("read_at")
    private String readAt;
    @SerializedName("notifiable_id")
    private int notifiableId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("data")
    private NotificationDataApiResponse dataResponse;
    @SerializedName("head_time")
    private String headTime;
    @SerializedName("date")
    private String date;

    public String getId() {
        return id;
    }

    public int getNotifierId() {
        return notifierId;
    }

    public String getReadAt() {
        return readAt;
    }

    public int getNotifiableId() {
        return notifiableId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getHeadTime() {
        return headTime;
    }

    public String getDate() {
        return date;
    }

    public NotificationDataApiResponse getDataResponse() {
        return dataResponse;
    }
}