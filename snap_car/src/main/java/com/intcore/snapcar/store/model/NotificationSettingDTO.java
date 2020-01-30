package com.intcore.snapcar.store.model;

import com.google.gson.annotations.SerializedName;

public class NotificationSettingDTO {

    @SerializedName("match_car_notify")
    private boolean matchCarNotify;
    @SerializedName("chat_notify")
    private boolean chatNotify;
    @SerializedName("offer_notify")
    private boolean offerNotify;

    public NotificationSettingDTO(boolean matchCarNotify, boolean chatNotify, boolean offerNotify) {
        this.matchCarNotify = matchCarNotify;
        this.chatNotify = chatNotify;
        this.offerNotify = offerNotify;
    }

    public boolean isMatchCarNotify() {
        return matchCarNotify;
    }

    public boolean isChatNotify() {
        return chatNotify;
    }

    public boolean isOfferNotify() {
        return offerNotify;
    }

}