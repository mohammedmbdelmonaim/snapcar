package com.intcore.snapcar.core.chat.model.socket;

import com.intcore.snapcar.core.chat.model.constants.SocketDTOType;
import com.intcore.snapcar.core.chat.model.payload.PayloadModel;

public class SocketModel {

    @SocketDTOType
    private final int type;
    private final PayloadModel payloadModel;

    public SocketModel(int type, PayloadModel payloadModel) {
        this.type = type;
        this.payloadModel = payloadModel;
    }

    public int getType() {
        return type;
    }

    public PayloadModel getPayloadModel() {
        return payloadModel;
    }

}