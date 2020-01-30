package com.intcore.snapcar.core.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceDTO implements Parcelable {

    public static final Creator<PlaceDTO> CREATOR = new Creator<PlaceDTO>() {
        @Override
        public PlaceDTO createFromParcel(Parcel in) {
            return new PlaceDTO(in);
        }

        @Override
        public PlaceDTO[] newArray(int size) {
            return new PlaceDTO[size];
        }
    };
    public static String TAG = "PlaceDto";
    private final String address;
    private final double latitude;
    private final double longitude;

    public PlaceDTO(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private PlaceDTO(Parcel in) {
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
