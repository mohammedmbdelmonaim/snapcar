package com.intcore.snapcar.store.model.carcolor;

public class CarColorModel {

    private int id;
    private String hex;
    private String nameAr;
    private String nameEn;

    CarColorModel(int id, String hex, String nameAr, String nameEn) {
        this.id = id;
        this.hex = hex;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }

    public static CarColorModel createDefault() {
        return new CarColorModel(0, "", "Show All", "Show All");
    }

    public int getId() {
        return id;
    }

    public String getHex() {
        return hex;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }
}