package com.intcore.snapcar.store.model.importer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class ImporterViewModel implements Parcelable {

    public static final Creator<ImporterViewModel> CREATOR = new Creator<ImporterViewModel>() {
        @Override
        public ImporterViewModel createFromParcel(Parcel in) {
            return new ImporterViewModel(in);
        }

        @Override
        public ImporterViewModel[] newArray(int size) {
            return new ImporterViewModel[size];
        }
    };
    private int id;
    private String name;

    ImporterViewModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private ImporterViewModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    private static boolean isEnglishLang() {
        return (Locale.getDefault().getLanguage().equals("en"));
    }

    public static ImporterViewModel createDefault() {
        if (isEnglishLang()) {
            return new ImporterViewModel(0, "Show All");
        } else {
            return new ImporterViewModel(0, "عرض الكل");
        }
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