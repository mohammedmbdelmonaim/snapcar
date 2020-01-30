package com.intcore.snapcar.store.model.category;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class CategoryViewModel implements Parcelable {

    public static final Creator<CategoryViewModel> CREATOR = new Creator<CategoryViewModel>() {
        @Override
        public CategoryViewModel createFromParcel(Parcel in) {
            return new CategoryViewModel(in);
        }

        @Override
        public CategoryViewModel[] newArray(int size) {
            return new CategoryViewModel[size];
        }
    };
    private int id;
    private String name;

    CategoryViewModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private CategoryViewModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static CategoryViewModel createDefault() {
        if (isEnglishLang()) {
            return new CategoryViewModel(0, "Show All");
        } else {
            return new CategoryViewModel(0, "عرض الكل");
        }
    }

    private static boolean isEnglishLang() {
        return (Locale.getDefault().getLanguage().equals("en"));
    }

    public int getId() {
        return id;
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
        dest.writeString(name);
    }
}