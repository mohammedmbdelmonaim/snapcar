package com.intcore.snapcar.core.chat.model.message;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import com.intcore.snapcar.core.chat.model.constants.AttachmentStatus;
import com.intcore.snapcar.core.chat.model.constants.MessageStatus;
import com.intcore.snapcar.core.chat.model.constants.MessageType;
import com.intcore.snapcar.core.chat.model.user.UserEntity;
import com.intcore.snapcar.core.chat.model.user.UserTypeConverter;

import java.util.UUID;

@Entity(tableName = "chatMessage")
public class MessageEntity {

    @PrimaryKey
    @NonNull
    private final String localId;
    @MessageStatus
    private final int messageStatus;
    @AttachmentStatus
    private final int attachmentStatus;
    private final int consultationId;
    private final String attachmentUrl;
    private final String thumbnailUrl;
    private final String updatedAt;
    private final String createdAt;
    @ColumnInfo(name = "serverId")
    private final String serverId;
    private final int messageType;
    private final long unixTime;
    private final String text;
    @TypeConverters(UserTypeConverter.class)
    private final UserEntity userEntity;

    MessageEntity(int attachmentStatus,
                  int consultationId,
                  String text,
                  String attachmentUrl,
                  int messageType,
                  @NonNull String localId,
                  int messageStatus,
                  String updatedAt,
                  String createdAt,
                  String serverId,
                  long unixTime,
                  String thumbnailUrl,
                  UserEntity userEntity) {
        this.attachmentStatus = attachmentStatus;
        this.consultationId = consultationId;
        this.attachmentUrl = attachmentUrl;
        this.messageStatus = messageStatus;
        this.thumbnailUrl = thumbnailUrl;
        this.messageType = messageType;
        this.userEntity = userEntity;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.serverId = serverId;
        this.unixTime = unixTime;
        this.localId = localId;
        this.text = text;
    }

    public static String generateLocalId() {
        return UUID.randomUUID().toString();
    }

    public Builder builder() {
        return new Builder(this.localId)
                .messageStatus(this.messageStatus)
                .consultationId(this.consultationId)
                .text(this.text)
                .unixTime(this.unixTime)
                .attachmentUrl(this.attachmentUrl)
                .messageType(this.messageType)
                .updatedAt(this.updatedAt)
                .createdAt(this.createdAt)
                .serverId(this.serverId)
                .attachmentStatus(this.attachmentStatus)
                .userEntity(this.userEntity);
    }

    public long getUnixTime() {
        return unixTime;
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

    @AttachmentStatus
    public int getAttachmentStatus() {
        return attachmentStatus;
    }

    @NonNull
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    @MessageStatus
    public int getMessageStatus() {
        return messageStatus;
    }

    @Override
    public String toString() {
        return "Entity" +
                "\nMessage status: " + messageStatus
                + "\nAttachment status: " + attachmentStatus
                + "\nConsultation ID: " + consultationId
                + "\nText: " + text
                + "\nAttachmentUrl: " + attachmentUrl
                + "\nThumbnail: " + thumbnailUrl
                + "\nType: " + messageType
                + "\nLocalId: " + localId
                + "\nUpdatedAt: " + updatedAt
                + "\nCreatedAt: " + createdAt
                + "\nServerId: " + serverId;
    }

    public static class Builder {

        private final String localId;
        private String thumbnailUrl;
        private int consultationId;
        private String text;
        private String attachmentUrl;
        private int messageType;
        private String updatedAt;
        private String createdAt;
        private String serverId;
        private int messageStatus;
        private int attachmentStatus;
        private long unixTime;
        @TypeConverters(UserTypeConverter.class)
        private UserEntity userEntity;

        public Builder(String localId) {
            this.localId = localId;
        }

        public Builder consultationId(int consultationId) {
            this.consultationId = consultationId;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder unixTime(long friendlyTime) {
            this.unixTime = friendlyTime;
            return this;
        }

        public Builder attachmentStatus(@AttachmentStatus int attachmentStatus) {
            this.attachmentStatus = attachmentStatus;
            return this;
        }

        public Builder attachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
            return this;
        }

        public Builder messageType(@MessageType int messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder messageStatus(@MessageStatus int messageStatus) {
            this.messageStatus = messageStatus;
            return this;
        }

        public Builder updatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder serverId(String serverId) {
            this.serverId = serverId;
            return this;
        }

        public Builder userEntity(UserEntity userEntity) {
            this.userEntity = userEntity;
            return this;
        }

        public Builder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public MessageEntity build() {
            return new MessageEntity(attachmentStatus, consultationId, text, attachmentUrl, messageType, localId,
                    messageStatus, updatedAt, createdAt, serverId, unixTime, thumbnailUrl, userEntity);
        }
    }
}