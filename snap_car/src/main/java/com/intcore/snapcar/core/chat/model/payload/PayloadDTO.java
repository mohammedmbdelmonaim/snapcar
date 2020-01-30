package com.intcore.snapcar.core.chat.model.payload;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.core.chat.model.message.MessageDTO;

public class PayloadDTO {

    @SerializedName("message")
    private MessageDTO messageDTO;

    @SerializedName("room_id")
    private int romId;

    @SerializedName("message_id")
    private int messageId;

    public PayloadDTO(int messageId, int roomId) {
        this.messageId = messageId;
        this.romId = roomId;
    }

    public PayloadDTO(MessageDTO messageDTO) {
        this.messageDTO = messageDTO;
    }

    public MessageDTO getMessageDTO() {
        return messageDTO;
    }

    public int getRomId() {
        return romId;
    }

    public int getMessageId() {
        return messageId;
    }
}