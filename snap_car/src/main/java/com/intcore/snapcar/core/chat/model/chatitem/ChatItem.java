package com.intcore.snapcar.core.chat.model.chatitem;

import com.intcore.snapcar.core.chat.model.constants.ChatItemType;

public abstract class ChatItem {

    @ChatItemType
    abstract public int getType();
}
