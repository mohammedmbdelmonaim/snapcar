package com.intcore.snapcar.store.model.carcolor;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class CarColorViewModel implements Parcelable {

    public static final Creator<CarColorViewModel> CREATOR = new Creator<CarColorViewModel>() {
        @Override
        public CarColorViewModel createFromParcel(Parcel in) {
            return new CarColorViewModel(in);
        }

        @Override
        public CarColorViewModel[] newArray(int size) {
            return new CarColorViewModel[size];
        }
    };
    private int id;
    private String hex;
    private String name;

    CarColorViewModel(int id, String hex, String name) {
        this.id = id;
        this.hex = hex;
        this.name = name;
    }

    private CarColorViewModel(Parcel in) {
        id = in.readInt();
        hex = in.readString();
        name = in.readString();
    }

    private static boolean isEnglishLang() {
        return (Locale.getDefault().getLanguage().equals("en"));
    }

    public static CarColorViewModel createDefault() {
        if (isEnglishLang()) {
            return new CarColorViewModel(0, "", "Show All");
        } else {
            return new CarColorViewModel(0, "", "عرض الكل");
        }
    }

    public int getId() {
        return id;
    }

    public String getHex() {
        return hex;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(hex);
        dest.writeString(name);
    }
}