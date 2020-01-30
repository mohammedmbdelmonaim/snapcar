package com.intcore.snapcar.ui.chatthread;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.ui.ActivityArgs;
import com.intcore.snapcar.core.chat.model.PlaceDTO;

public class ChatThreadActivityArgs implements ActivityArgs {

    private final static String CHAT_ID_KEY = "ChatIdKey";
    private final static String MESSAGE_ID_KEY = "MessageIdKey";
    private final static String USER_NAME_KEY = "UserNameKey";
    private final static String PLACE_KEY = "PlaceKey";
    private PlaceDTO placeDTO;
    private String userName;
    private int messageId;
    private int roomId;

    public ChatThreadActivityArgs(int roomId, int messageId, String userName, PlaceDTO placeDTO) {
        this.messageId = messageId;
        this.placeDTO = placeDTO;
        this.userName = userName;
        this.roomId = roomId;
    }

    public static ChatThreadActivityArgs deserializeFrom(Intent intent) {
        return new ChatThreadActivityArgs(intent.getIntExtra(CHAT_ID_KEY, 0),
                intent.getIntExtra(MESSAGE_ID_KEY, 0),
                intent.getStringExtra(USER_NAME_KEY),
                intent.getParcelableExtra(PLACE_KEY));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, ChatThreadActivity.class);
        intent.putExtra(CHAT_ID_KEY, roomId);
        intent.putExtra(MESSAGE_ID_KEY, messageId);
        intent.putExtra(USER_NAME_KEY, userName);
        intent.putExtra(PLACE_KEY, placeDTO);
        return intent;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getUserName() {
        return userName;
    }

    public int getMessageId() {
        return messageId;
    }

    public PlaceDTO getPlaceDTO() {
        return placeDTO;
    }
}