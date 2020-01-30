package com.intcore.snapcar.store.model.showroom;

import com.google.android.gms.maps.model.LatLng;
import com.intcore.snapcar.store.model.hotzone.HotZoneModel;

public class ShowRoomInfoModel {

    private final String openTimeTwo;
    private final String closedTimeTwo;
    private final String openTime;
    private final String closedTime;
    private final String longitude;
    private final String latitude;
    private final String userId;
    private final Boolean isPremium;
    private final String verifyLatter;
    private final String phones;
    private final String dealingWith;
    private final HotZoneModel hotZoneModel;
    private final String phonesPostions;

    ShowRoomInfoModel(String openTimeTwo,
                      String closeTimeTwo,
                      String openTime,
                      String closedTime,
                      String longitude,
                      String latitude,
                      String userId,
                      String verifyLatter,
                      String phones,
                      String dealingWith,
                      Boolean isPremium,
                      HotZoneModel hotZoneModel
            , String phonesPostions) {
        this.openTime = openTime;
        this.closedTime = closedTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userId = userId;
        this.verifyLatter = verifyLatter;
        this.phones = phones;
        this.isPremium = isPremium;
        this.closedTimeTwo = closeTimeTwo;
        this.openTimeTwo = openTimeTwo;
        this.dealingWith = dealingWith;
        this.hotZoneModel = hotZoneModel;
        this.phonesPostions = phonesPostions;
    }

    public String getPhonesPostions() {
        return phonesPostions;
    }

    public String getClosedTimeTwo() {
        return closedTimeTwo;
    }

    public String getOpenTimeTwo() {
        return openTimeTwo;
    }

    public String getDealingWith() {
        return dealingWith;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getClosedTime() {
        return closedTime;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getUserId() {
        return userId;
    }

    public String getVerifyLatter() {
        return verifyLatter;
    }

    public String getPhones() {
        return phones;
    }

    public HotZoneModel getHotZoneModel() {
        return hotZoneModel;
    }

    public LatLng getLocation(){
        if (latitude == null || longitude == null || latitude.isEmpty() || longitude.isEmpty())
            return null;

        return new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }
}
