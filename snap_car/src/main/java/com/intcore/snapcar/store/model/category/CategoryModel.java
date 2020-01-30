package com.intcore.snapcar.store.model.category;

public class CategoryModel {

    private int id;
    private String nameAr;
    private String nameEn;

    CategoryModel(int id, String nameAr, String nameEn) {
        this.id = id;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }

    public static CategoryModel createDefault() {
        return new CategoryModel(0, "Show all", "Show all");
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
}