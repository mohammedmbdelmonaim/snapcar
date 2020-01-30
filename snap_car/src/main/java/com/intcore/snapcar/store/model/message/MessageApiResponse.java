package com.intcore.snapcar.store.model.message;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;

public class MessageApiResponse {

    @SerializedName("id")
    private int messageId;
    @SerializedName("room_id")
    private int roomId;
    @SerializedName("sender_id")
    private int senderId;
    @SerializedName("message")
    private String message;
    @SerializedName("attachment")
    private String attachment;
    @SerializedName("type")
    private int type;
    @SerializedName("local_identifier")
    private String localIdentifier;
    @SerializedName("seen")
    private int seen;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("time")
    private String time;
    @SerializedName("sender")
    private DefaultUserDataApiResponse.DefaultUserApiResponse senderApiResponse;

    public int getMessageId() {
        return messageId;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getAttachment() {
        return attachment;
    }

    public int getType() {
        return type;
    }

    public String getLocalIdentifier() {
        return localIdentifier;
    }

    public int getSeen() {
        return seen;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getTime() {
        return time;
    }

    public DefaultUserDataApiResponse.DefaultUserApiResponse getSenderApiResponse() {
        return senderApiResponse;
    }
}