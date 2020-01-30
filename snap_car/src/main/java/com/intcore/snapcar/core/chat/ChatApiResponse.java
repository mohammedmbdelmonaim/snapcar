package com.intcore.snapcar.core.chat;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.core.chat.model.message.MessageDTO;
import com.intcore.snapcar.core.chat.model.user.UserDataApiResponse;

public class ChatApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("user_id_1")
    private int userIdOne;
    @SerializedName("user_id_2")
    private int userIdTwo;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("date")
    private String date;
    @SerializedName("user")
    private UserDataApiResponse.UserApiResponse userApiResponse;
    @SerializedName("seen")
    private int seen;
    @SerializedName("last_message_time")
    private String lastMessageTime;
    @SerializedName("last_message")
    private MessageDTO messageApiResponse;

    public int getId() {
        return id;
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

    public String getDate() {
        return date;
    }

    public UserDataApiResponse.UserApiResponse getUserApiResponse() {
        return userApiResponse;
    }

    public int getSeen() {
        return seen;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public MessageDTO getMessageApiResponse() {
        return messageApiResponse;
    }
}