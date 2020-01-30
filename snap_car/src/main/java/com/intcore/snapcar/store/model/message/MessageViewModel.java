package com.intcore.snapcar.store.model.message;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;

public class MessageViewModel {

    private int type;
    private int seen;
    private int roomId;
    private String time;
    private int senderId;
    private int messageId;
    private String message;
    private String attachment;
    private DefaultUserModel senderViewModel;

    MessageViewModel(int type,
                     int seen,
                     int roomId,
                     String time,
                     int senderId,
                     int messageId,
                     String message,
                     String attachment,
                     DefaultUserModel senderViewModel) {
        this.type = type;
        this.seen = seen;
        this.time = time;
        this.roomId = roomId;
        this.message = message;
        this.senderId = senderId;
        this.messageId = messageId;
        this.attachment = attachment;
        this.senderViewModel = senderViewModel;
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

    public String getAttachment() {
        return attachment;
    }

    public DefaultUserModel getSenderViewModel() {
        return senderViewModel;
    }
}