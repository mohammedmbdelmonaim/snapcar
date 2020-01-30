package com.intcore.snapcar.store.model.country;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CountryViewModel implements Parcelable {

    public static final Creator<CountryViewModel> CREATOR = new Creator<CountryViewModel>() {
        @Override
        public CountryViewModel createFromParcel(Parcel in) {
            return new CountryViewModel(in);
        }

        @Override
        public CountryViewModel[] newArray(int size) {
            return new CountryViewModel[size];
        }
    };
    private List<CountryViewModel> countryViewModels;
    private String phoneRegex;
    private String phoneCode;
    private String image;
    private String name;
    private int id;

    CountryViewModel(int id, String phoneCode, String image, String name, String phoneRegex, List<CountryViewModel> countryViewModels) {
        this.countryViewModels = countryViewModels;
        this.phoneRegex = phoneRegex;
        this.phoneCode = phoneCode;
        this.image = image;
        this.name = name;
        this.id = id;
    }

    CountryViewModel(int id, String phoneCode, String image, String name, String phoneRegex) {
        this.phoneRegex = phoneRegex;
        this.phoneCode = phoneCode;
        this.image = image;
        this.name = name;
        this.id = id;
    }

    protected CountryViewModel(Parcel in) {
        countryViewModels = in.createTypedArrayList(CountryViewModel.CREATOR);
        phoneRegex = in.readString();
        phoneCode = in.readString();
        image = in.readString();
        name = in.readString();
        id = in.readInt();
    }

    private static boolean isEnglishLang() {
        return (Locale.getDefault().getLanguage().equals("en"));
    }

    public static CountryViewModel createDefault() {
        if (isEnglishLang()) {
            return new CountryViewModel(0, "", "", "Show All", "10");
        } else {
            return new CountryViewModel(0, "", "", "عرض الكل", "10");
        }
    }

    public static CountryViewModel createDefaultWithList() {
        if (isEnglishLang()) {
            return new CountryViewModel(0, "", "", "Show All", "10", Collections.emptyList());
        } else {
            return new CountryViewModel(0, "", "", "عرض الكل", "10", Collections.emptyList());
        }
    }

    public List<CountryViewModel> getCountryViewModels() {
        return countryViewModels;
    }

    public String getPhoneRegex() {
        return phoneRegex;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(countryViewModels);
        dest.writeString(phoneRegex);
        dest.writeString(phoneCode);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeInt(id);
    }
}