package com.intcore.snapcar.store.model.country;

import java.util.Collections;
import java.util.List;

public class CountryModel {

    private int id;
    private String nameAr;
    private String image;
    private String nameEn;
    private String phoneCode;
    private String phoneRegex;
    private List<CountryModel> countryModels;

    CountryModel(int id, String nameAr, String image, String nameEn, String phoneCode, String phoneRegex, List<CountryModel> countryModels) {
        this.id = id;
        this.image = image;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
        this.phoneCode = phoneCode;
        this.phoneRegex = phoneRegex;
        this.countryModels = countryModels;
    }

    CountryModel(int id, String nameAr, String image, String nameEn, String phoneCode, String phoneRegex) {
        this.id = id;
        this.image = image;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
        this.phoneCode = phoneCode;
        this.phoneRegex = phoneRegex;
    }

    public static CountryModel createDefault() {
        return new CountryModel(0, "Show All", "", "Show All", "00", "10");
    }

    public static CountryModel createDefaultWithList() {
        return new CountryModel(0, "Show All", "", "Show All", "00", "10", Collections.emptyList());
    }

    public int getId() {
        return id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public String getImage() {
        return image;
    }

    public List<CountryModel> getCountryModels() {
        return countryModels;
    }

    public String getPhoneRegex() {
        return phoneRegex;
    }
}