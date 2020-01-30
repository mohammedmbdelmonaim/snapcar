package com.intcore.snapcar.core.chat;

import com.intcore.snapcar.core.chat.model.message.MessageViewModel;
import com.intcore.snapcar.core.chat.model.user.UserViewModel;

public class ChatViewModel {

    private int id;
    private int seen;
    private String date;
    private int userIdOne;
    private int userIdTwo;
    private String createdAt;
    private String updatedAt;
    private String lastMessageTime;
    private UserViewModel userViewModel;
    private MessageViewModel messageViewModel;

    ChatViewModel(int id,
                  int seen,
                  String date,
                  int userIdOne,
                  int userIdTwo,
                  String createdAt,
                  String updatedAt,
                  String lastMessageTime,
                  UserViewModel userViewModel,
                  MessageViewModel messageViewModel) {
        this.id = id;
        this.seen = seen;
        this.date = date;
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userViewModel = userViewModel;
        this.lastMessageTime = lastMessageTime;
        this.messageViewModel = messageViewModel;
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

    public UserViewModel getUserViewModel() {
        return userViewModel;
    }

    public MessageViewModel getMessageViewModel() {
        return messageViewModel;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj instanceof ChatViewModel) {
            ChatViewModel viewModel = (ChatViewModel) obj;
            isEqual = viewModel.getId() == this.id
                    && viewModel.getCreatedAt().contentEquals(this.createdAt)
                    && viewModel.getSeen() == this.seen
                    && viewModel.getDate().contentEquals(this.date)
                    && viewModel.getUpdatedAt().contentEquals(this.updatedAt)
                    && viewModel.getLastMessageTime().contentEquals(this.lastMessageTime)
                    && viewModel.getUserIdOne() == this.userIdOne
                    && viewModel.getUserIdTwo() == this.userIdTwo;
        }
        return isEqual;
    }
}