package com.intcore.snapcar.core.chat.model.chatitem;

import com.intcore.snapcar.core.chat.model.message.MessageViewModel;

public class VoiceNoteChatItem extends ChatItem {

    private final int messageType;
    private final MessageViewModel messageViewModel;

    VoiceNoteChatItem(int messageType, MessageViewModel messageViewModel) {
        this.messageType = messageType;
        this.messageViewModel = messageViewModel;
    }

    @Override
    public int getType() {
        return messageType;
    }

    public MessageViewModel getMessageViewModel() {
        return messageViewModel;
    }
}
