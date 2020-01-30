package com.intcore.snapcar.store.model.images;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageViewModel implements Parcelable {

    public static final Creator<ImageViewModel> CREATOR = new Creator<ImageViewModel>() {
        @Override
        public ImageViewModel createFromParcel(Parcel in) {
            return new ImageViewModel(in);
        }

        @Override
        public ImageViewModel[] newArray(int size) {
            return new ImageViewModel[size];
        }
    };
    private int id;
    private int carId;
    private String place;
    private int isMain;
    private String image;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;

    ImageViewModel(int id,
                   int carId,
                   String place,
                   int isMain,
                   String image,
                   String deletedAt,
                   String createdAt,
                   String updatedAt) {
        this.id = id;
        this.carId = carId;
        this.place = place;
        this.isMain = isMain;
        this.image = image;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private ImageViewModel(Parcel in) {
        id = in.readInt();
        carId = in.readInt();
        place = in.readString();
        isMain = in.readInt();
        image = in.readString();
        deletedAt = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public int getId() {
        return id;
    }

    public int getCarId() {
        return carId;
    }

    public String getImage() {
        return image;
    }

    public String getPlace() {
        return place;
    }

    public int getIsMain() {
        return isMain;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(carId);
        dest.writeString(place);
        dest.writeInt(isMain);
        dest.writeString(image);
        dest.writeString(deletedAt);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }
}
