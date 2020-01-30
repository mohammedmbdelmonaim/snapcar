package com.intcore.snapcar.store.model.showroom;

public class ShowRoomInfoViewModel {
    private final String openTime;

    ShowRoomInfoViewModel(String openTime) {
        this.openTime = openTime;
    }

    public String getOpenTime() {
        return openTime;
    }
}
