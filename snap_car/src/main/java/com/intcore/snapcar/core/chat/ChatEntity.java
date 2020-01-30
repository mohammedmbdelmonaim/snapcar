package com.intcore.snapcar.core.chat;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.intcore.snapcar.core.chat.model.message.MessageEntity;
import com.intcore.snapcar.core.chat.model.user.UserEntity;
import com.intcore.snapcar.core.chat.model.user.UserTypeConverter;

@Entity(tableName = "chatTable")
public class ChatEntity {

    @PrimaryKey
    private int id;
    private int seen;
    private String date;
    private int userIdOne;
    private int userIdTwo;
    private String createdAt;
    private String updatedAt;
    private String lastMessageTime;
    @TypeConverters(UserTypeConverter.class)
    private MessageEntity messageModel;
    @TypeConverters(UserTypeConverter.class)
    private UserEntity userEntity;

    ChatEntity(int id,
               int seen,
               String date,
               int userIdOne,
               int userIdTwo,
               String createdAt,
               String updatedAt,
               String lastMessageTime,
               MessageEntity messageModel,
               UserEntity userEntity) {
        this.id = id;
        this.seen = seen;
        this.date = date;
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userEntity = userEntity;
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

    public MessageEntity getMessageModel() {
        return messageModel;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }
}