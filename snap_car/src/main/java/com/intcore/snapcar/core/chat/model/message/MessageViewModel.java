package com.intcore.snapcar.core.chat.model.message;

import com.intcore.snapcar.core.chat.model.constants.AttachmentStatus;
import com.intcore.snapcar.core.chat.model.constants.MessageStatus;
import com.intcore.snapcar.core.chat.model.constants.MessageType;
import com.intcore.snapcar.core.chat.model.user.UserViewModel;

public class MessageViewModel {

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
    private final String friendlyTime;
    private final String thumbnail;
    private final UserViewModel userViewModel;

    MessageViewModel(int messageStatus,
                     int attachmentStatus,
                     int consultationId,
                     String text,
                     String attachmentUrl,
                     int messageType,
                     String localId,
                     String updatedAt,
                     String createdAt,
                     String serverId,
                     String friendlyTime,
                     String thumbnail,
                     UserViewModel userViewModel) {
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
        this.friendlyTime = friendlyTime;
        this.thumbnail = thumbnail;
        this.userViewModel = userViewModel;
    }

    public String getFriendlyTime() {
        return friendlyTime;
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

    public UserViewModel getUserViewModel() {
        return userViewModel;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj == null) return true;
        if (obj instanceof MessageViewModel) {
            MessageViewModel viewModel = (MessageViewModel) obj;
            isEqual = viewModel.getMessageStatus() == this.messageStatus
                    && viewModel.getLocalId().contentEquals(this.localId);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + messageType + messageStatus;
    }

    @Override
    public String toString() {
        return "ViewModel" +
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