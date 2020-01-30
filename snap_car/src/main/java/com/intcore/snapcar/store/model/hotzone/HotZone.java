package com.intcore.snapcar.store.model.hotzone;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.core.chat.model.user.UserDataApiResponse;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;

public class HotZone {

    @SerializedName("id")
    public Integer id;
    @SerializedName("name")
    public String name;
    @SerializedName("phone")
    public String phone;
    @SerializedName("provide_discount")
    public Integer provideDiscount;
    @SerializedName("activation")
    public Integer activation;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("user_id")
    public Integer userId;
    @SerializedName("gender")
    public Integer gender;
    @SerializedName("deleted_at")
    public String deletedAt;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("updated_at")
    public String updatedAt;
    @SerializedName("is_premium")
    public Boolean isPremium;
    @SerializedName("is_showroom")
    public Boolean isShowroom;
    @SerializedName("image")
    public String image;
    @SerializedName("user")
    public DefaultUserDataApiResponse.DefaultUserApiResponse user;
    //custom
    private int zoneType;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getProvideDiscount() {
        return provideDiscount;
    }

    public Integer getActivation() {
        return activation;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public Integer getUserId() {
        return userId;
    }

    public Boolean  getShowroom() {
        return isShowroom;
    }

    public Integer getGender() {
        return gender;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Boolean isPremium() {
        return isPremium;
    }

    public String getImage() {
        return image;
    }
    public DefaultUserDataApiResponse.DefaultUserApiResponse getUser() {
        return user;
    }


    public LatLng getLocation(){
        if (latitude == null || longitude == null || latitude.isEmpty() || longitude.isEmpty())
            return null;

        return new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }

    public int getZoneType() {
        return zoneType;
    }

    public void setZoneType(int zoneType) {
        this.zoneType = zoneType;
    }
}