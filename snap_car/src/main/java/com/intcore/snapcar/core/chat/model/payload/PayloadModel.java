package com.intcore.snapcar.core.chat.model.payload;

import com.intcore.snapcar.core.chat.model.message.MessageDTO;

public class PayloadModel {

    private MessageDTO messageDTO;
    private int romId;

    public PayloadModel(MessageDTO messageDTO, int romId) {
        this.messageDTO = messageDTO;
        this.romId = romId;
    }

    public int getRomId() {
        return romId;
    }

    public MessageDTO getMessageDTO() {
        return messageDTO;
    }
}