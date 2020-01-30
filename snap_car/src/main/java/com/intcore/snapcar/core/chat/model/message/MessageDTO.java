package com.intcore.snapcar.core.chat.model.message;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.core.chat.model.constants.MessageType;
import com.intcore.snapcar.core.chat.model.user.UserDataApiResponse;

public class MessageDTO {

    @SerializedName("room_id")
    private int consultationId;

    @SerializedName("message")
    private String text;

    @SerializedName("attachment")
    private String attachmentUrl;

    @SerializedName("type")
    private int messageType;

    @SerializedName("local_identifier")
    private String localId;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("id")
    private String serverId;

    @SerializedName("time")
    private long unixTime;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("sender")
    private UserDataApiResponse.UserApiResponse sender;

    MessageDTO(int consultationId, String text, String attachmentUrl, int messageType,
               String localId, String updatedAt, String createdAt, String serverId,
               long unixTime, String thumbnailUrl, UserDataApiResponse.UserApiResponse sender) {
        this.consultationId = consultationId;
        this.attachmentUrl = attachmentUrl;
        this.messageType = messageType;
        this.thumbnail = thumbnailUrl;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.serverId = serverId;
        this.unixTime = unixTime;
        this.localId = localId;
        this.sender = sender;
        this.text = text;
    }

    public UserDataApiResponse.UserApiResponse getSender() {
        return sender;
    }

    public int getConsultationId() {
        return consultationId;
    }

    public String getText() {
        return text;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    @MessageType
    public int getMessageType() {
        return messageType;
    }

    public String getLocalId() {
        return localId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getServerId() {
        return serverId;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}