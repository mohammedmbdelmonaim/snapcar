package com.intcore.snapcar.store.model.hotzone;

import android.os.Parcel;
import android.os.Parcelable;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;

public class HotZoneViewModel implements Parcelable {

    public static final Creator<HotZoneViewModel> CREATOR = new Creator<HotZoneViewModel>() {
        @Override
        public HotZoneViewModel createFromParcel(Parcel in) {
            return new HotZoneViewModel(in);
        }

        @Override
        public HotZoneViewModel[] newArray(int size) {
            return new HotZoneViewModel[size];
        }
    };
    private Integer id;
    private String name;
    private String phone;
    private String image;
    private Integer userId;
    private Integer gender;
    private String latitude;
    private String longitude;
    private String createdAt;
    private String updatedAt;
    private Boolean isPremium;
    private Integer activation;
    private Integer provideDiscount;
    private DefaultUserModel showRoomModel ;
    private Boolean isShowroom;

    public HotZoneViewModel(Integer id,
                            String name,
                            String phone,
                            String image,
                            Integer userId,
                            Integer gender,
                            String latitude,
                            String longitude,
                            String createdAt,
                            String updatedAt,
                            Boolean isPremium,
                            Integer activation,
                            Integer provideDiscount,
                            Boolean isShowroom,
                            DefaultUserModel showRoomModel ) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.userId = userId;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isPremium = isPremium;
        this.activation = activation;
        this.provideDiscount = provideDiscount;
        this.isShowroom = isShowroom;
        this.showRoomModel = showRoomModel ;
    }

    protected HotZoneViewModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        phone = in.readString();
        image = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        if (in.readByte() == 0) {
            gender = null;
        } else {
            gender = in.readInt();
        }
        latitude = in.readString();
        longitude = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        byte tmpIsPremium = in.readByte();
        isPremium = tmpIsPremium == 0 ? null : tmpIsPremium == 1;
        if (in.readByte() == 0) {
            activation = null;
        } else {
            activation = in.readInt();
        }
        if (in.readByte() == 0) {
            provideDiscount = null;
        } else {
            provideDiscount = in.readInt();
        }
    }

    public Boolean isShowroom() {
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

    public String getImage() {
        return image;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getGender() {
        return gender;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public Integer getActivation() {
        return activation;
    }

    public Integer getProvideDiscount() {
        return provideDiscount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(image);
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        if (gender == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(gender);
        }
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeByte((byte) (isPremium == null ? 0 : isPremium ? 1 : 2));
        if (activation == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(activation);
        }
        if (provideDiscount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(provideDiscount);
        }
    }
}