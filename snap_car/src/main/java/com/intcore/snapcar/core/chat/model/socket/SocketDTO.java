package com.intcore.snapcar.core.chat.model.socket;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.core.chat.model.constants.SocketDTOType;
import com.intcore.snapcar.core.chat.model.payload.PayloadDTO;

public class SocketDTO {

    @SerializedName("type")
    @SocketDTOType
    private int type;

    @SerializedName("payload")
    private PayloadDTO payload;

    public SocketDTO(int type, PayloadDTO payload) {
        this.type = type;
        this.payload = payload;
    }

    @SocketDTOType
    public int getType() {
        return type;
    }

    public PayloadDTO getPayload() {
        return payload;
    }
}