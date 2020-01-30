package com.intcore.snapcar.store.model.importer;

public class ImporterModel {

    private int id;
    private String nameAr;
    private String nameEn;

    ImporterModel(int id, String nameAr, String nameEn) {
        this.id = id;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }

    public static ImporterModel createDefault() {
        return new ImporterModel(0, "Show all", "Show All");
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
