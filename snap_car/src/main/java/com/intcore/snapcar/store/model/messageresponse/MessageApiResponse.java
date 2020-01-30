package com.intcore.snapcar.store.model.messageresponse;

import com.google.gson.annotations.SerializedName;

public class MessageApiResponse {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}
