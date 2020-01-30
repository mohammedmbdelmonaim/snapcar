package com.intcore.snapcar.store.model.showroom;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.hotzone.HotZone;

public class ShowRoomInfoApiResponse {

    @SerializedName("open_time")
    private String openTime;
    @SerializedName("close_time")
    private String closedTime;
    @SerializedName("open_time_2")
    private String openTimeTwo;
    @SerializedName("close_time_2")
    private String closedTimeTwo;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("verify_latter")
    private String verifyLatter;
    @SerializedName("phones_custom")
    private String phones;
    @SerializedName("dealing_with")
    private String dealingWith;
    @SerializedName("is_premium")
    private Boolean isPremium;
    @SerializedName("hotzone")
    private HotZone hotZone;
    @SerializedName("positions")
    String phonesPositions;
    @SerializedName("hotzone_id")
    private int hotZoneId;

    public String getPhonesPositions() {
        return phonesPositions;
    }

    public HotZone getHotZone() {
        return hotZone;
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

    public String getClosedTimeTwo() {
        return closedTimeTwo;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public int getHotZoneId() {
        return hotZoneId;
    }

    public String getOpenTimeTwo() {
        return openTimeTwo;
    }

    public String getPhones() {
        return phones;
    }

    public LatLng getLocation(){
        if (latitude == null || longitude == null || latitude.isEmpty() || longitude.isEmpty())
            return null;

        return new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }


}
