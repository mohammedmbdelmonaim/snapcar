package com.intcore.snapcar.store.model.message;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;

public class MessageModel {

    private int type;
    private int seen;
    private int roomId;
    private String time;
    private int senderId;
    private int messageId;
    private String message;
    private String createdAt;
    private String updatedAt;
    private String attachment;
    private String localIdentifier;
    private DefaultUserModel senderModel;

    MessageModel(int type,
                 int seen,
                 int roomId,
                 String time,
                 int senderId,
                 int messageId,
                 String message,
                 String createdAt,
                 String updatedAt,
                 String attachment,
                 String localIdentifier,
                 DefaultUserModel senderModel) {
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.roomId = roomId;
        this.message = message;
        this.senderId = senderId;
        this.messageId = messageId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.attachment = attachment;
        this.senderModel = senderModel;
        this.localIdentifier = localIdentifier;
    }

    public int getType() {
        return type;
    }

    public int getSeen() {
        return seen;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getTime() {
        return time;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getAttachment() {
        return attachment;
    }

    public String getLocalIdentifier() {
        return localIdentifier;
    }

    public DefaultUserModel getSenderModel() {
        return senderModel;
    }
}