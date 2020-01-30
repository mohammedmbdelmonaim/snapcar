package com.intcore.snapcar.store.model.cartracking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class CarTrackingViewModel implements Parcelable {

    public static final Creator<CarTrackingViewModel> CREATOR = new Creator<CarTrackingViewModel>() {
        @Override
        public CarTrackingViewModel createFromParcel(Parcel in) {
            return new CarTrackingViewModel(in);
        }

        @Override
        public CarTrackingViewModel[] newArray(int size) {
            return new CarTrackingViewModel[size];
        }
    };
    private int id;
    private int carId;
    private LatLng oldLocation;
    private LatLng newLocation;

    CarTrackingViewModel(int id, int carId, LatLng oldLocation, LatLng newLocation) {
        this.id = id;
        this.carId = carId;
        this.oldLocation = oldLocation;
        this.newLocation = newLocation;
    }

    private CarTrackingViewModel(Parcel in) {
        id = in.readInt();
        carId = in.readInt();
        oldLocation = in.readParcelable(LatLng.class.getClassLoader());
        newLocation = in.readParcelable(LatLng.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public int getCarId() {
        return carId;
    }

    public LatLng getOldLocation() {
        return oldLocation;
    }

    public LatLng getNewLocation() {
        return newLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(carId);
        dest.writeParcelable(oldLocation, flags);
        dest.writeParcelable(newLocation, flags);
    }
}