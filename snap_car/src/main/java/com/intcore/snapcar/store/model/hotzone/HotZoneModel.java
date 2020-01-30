package com.intcore.snapcar.store.model.hotzone;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;

public class HotZoneModel {

    private Integer id;
    private String name;
    private String phone;
    private String image;
    private Integer userId;
    private Integer gender;
    private String latitude;
    private String longitude;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;
    private Boolean isPremium;
    private Boolean isShowroom;
    private Integer activation;
    private Integer provideDiscount;
    private DefaultUserModel showRoomModel ;

    HotZoneModel(Integer id,
                 String name,
                 String phone,
                 Integer userId,
                 Integer gender,
                 String latitude,
                 String longitude,
                 String deletedAt,
                 String createdAt,
                 String updatedAt,
                 Boolean isPremium,
                 Integer activation,
                 Integer provideDiscount,
                 String image,boolean isShowroom , DefaultUserModel showRoomModel) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.userId = userId;
        this.gender = gender;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isPremium = isPremium;
        this.activation = activation;
        this.provideDiscount = provideDiscount;
        this.isShowroom = isShowroom;
        this.showRoomModel = showRoomModel ;
    }


    public Boolean getShowroom() {
        return isShowroom;
    }
    public DefaultUserModel getShowRoomModel() {
        return showRoomModel;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getGender() {
        return gender;
    }

    public String getImage() {
        return image;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
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

    public Integer getActivation() {
        return activation;
    }

    public Integer getProvideDiscount() {
        return provideDiscount;
    }


}