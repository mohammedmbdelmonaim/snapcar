package com.intcore.snapcar.core.chat.model.message;

import com.intcore.snapcar.core.chat.model.constants.AttachmentStatus;
import com.intcore.snapcar.core.chat.model.constants.MessageStatus;
import com.intcore.snapcar.core.chat.model.constants.MessageType;
import com.intcore.snapcar.core.chat.model.user.UserModel;

public class MessageModel {

    @MessageStatus
    private final int messageStatus;
    @AttachmentStatus
    private final int attachmentStatus;
    private final int consultationId;
    private final String text;
    private final String attachmentUrl;
    private final int messageType;
    private final String localId;
    private final String updatedAt;
    private final String createdAt;
    private final String serverId;
    private final long unixTime;
    private final String thumbnail;
    private final UserModel userModel;

    MessageModel(int messageStatus, int attachmentStatus, int consultationId, String text, String attachmentUrl, int messageType,
                 String localId, String updatedAt, String createdAt, String serverId,
                 long unixTime, String thumbnail, UserModel userModel) {
        this.messageStatus = messageStatus;
        this.attachmentStatus = attachmentStatus;
        this.consultationId = consultationId;
        this.text = text;
        this.attachmentUrl = attachmentUrl;
        this.messageType = messageType;
        this.localId = localId;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.serverId = serverId;
        this.unixTime = unixTime;
        this.thumbnail = thumbnail;
        this.userModel = userModel;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public int getMessageStatus() {
        return messageStatus;
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

    public int getAttachmentStatus() {
        return attachmentStatus;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    @Override
    public String toString() {
        return "Model" +
                "\nMessage status: " + messageStatus
                + "\nConsultation ID: " + consultationId
                + "\nText: " + text
                + "\nAttachmentUrl: " + attachmentUrl
                + "\nType: " + messageType
                + "\nLocalId: " + localId
                + "\nUpdatedAt: " + updatedAt
                + "\nCreatedAt: " + createdAt
                + "\nServerId: " + serverId;
    }
}
