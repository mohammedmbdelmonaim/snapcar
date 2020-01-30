package com.intcore.snapcar.store.model.brands;

import android.os.Parcel;
import android.os.Parcelable;

import com.intcore.snapcar.store.model.model.ModelViewModel;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BrandsViewModel implements Parcelable {

    public static final Creator<BrandsViewModel> CREATOR = new Creator<BrandsViewModel>() {
        @Override
        public BrandsViewModel createFromParcel(Parcel in) {
            return new BrandsViewModel(in);
        }

        @Override
        public BrandsViewModel[] newArray(int size) {
            return new BrandsViewModel[size];
        }
    };
    private int id;
    private String name;
    private String image;
    private List<ModelViewModel> brandsModels;

    BrandsViewModel(int id, String name, String image, List<ModelViewModel> brandsModels) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.brandsModels = brandsModels;
    }

    private BrandsViewModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = in.readString();
        brandsModels = in.createTypedArrayList(ModelViewModel.CREATOR);
    }

    public static BrandsViewModel createDefault() {
        if (isEnglishLang()) {
            return new BrandsViewModel(0, "Show All", "", Collections.emptyList());
        } else {
            return new BrandsViewModel(0, "عرض الكل", "", Collections.emptyList());
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

    public String getImage() {
        return image;
    }

    public List<ModelViewModel> getBrandsModels() {
        return brandsModels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeTypedList(brandsModels);
    }
}