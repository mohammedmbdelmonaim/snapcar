package com.intcore.snapcar.core.chat;

import com.intcore.snapcar.core.chat.model.message.MessageModel;
import com.intcore.snapcar.core.chat.model.user.UserModel;

public class ChatModel {

    public static int isChatAlive = 0;
    private int id;
    private int seen;
    private String date;
    private int userIdOne;
    private int userIdTwo;
    private String createdAt;
    private String updatedAt;
    private String lastMessageTime;
    private UserModel useModel;
    private MessageModel messageModel;

    ChatModel(int id,
              int seen,
              String date,
              int userIdOne,
              int userIdTwo,
              String createdAt,
              String updatedAt,
              String lastMessageTime,
              UserModel useModel,
              MessageModel messageModel) {
        this.id = id;
        this.seen = seen;
        this.date = date;
        this.useModel = useModel;
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messageModel = messageModel;
        this.lastMessageTime = lastMessageTime;
    }

    public int getId() {
        return id;
    }

    public int getSeen() {
        return seen;
    }

    public String getDate() {
        return date;
    }

    public int getUserIdOne() {
        return userIdOne;
    }

    public int getUserIdTwo() {
        return userIdTwo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public UserModel getUseModel() {
        return useModel;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }
}