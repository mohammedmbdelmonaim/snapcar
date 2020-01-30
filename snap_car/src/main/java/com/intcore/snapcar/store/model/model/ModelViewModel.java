package com.intcore.snapcar.store.model.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.intcore.snapcar.store.model.category.CategoryViewModel;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ModelViewModel implements Parcelable {

    public static final Creator<ModelViewModel> CREATOR = new Creator<ModelViewModel>() {
        @Override
        public ModelViewModel createFromParcel(Parcel in) {
            return new ModelViewModel(in);
        }

        @Override
        public ModelViewModel[] newArray(int size) {
            return new ModelViewModel[size];
        }
    };
    private int id;
    private String name;
    private List<CategoryViewModel> categoryViewModels;

    ModelViewModel(int id, String name, List<CategoryViewModel> categoryViewModels) {
        this.id = id;
        this.name = name;
        this.categoryViewModels = categoryViewModels;
    }

    private ModelViewModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        categoryViewModels = in.createTypedArrayList(CategoryViewModel.CREATOR);
    }

    public static ModelViewModel createDefault() {
        if (isEnglishLang()) {
            return new ModelViewModel(0, "Show All", Collections.emptyList());
        } else {
            return new ModelViewModel(0, "عرض الكل", Collections.emptyList());
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

    public List<CategoryViewModel> getCategoryViewModels() {
        return categoryViewModels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(categoryViewModels);
    }
}