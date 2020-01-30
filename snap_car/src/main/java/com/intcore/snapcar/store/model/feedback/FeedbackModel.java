package com.intcore.snapcar.store.model.feedback;

public class FeedbackModel {


    private final int id;
    private final String nameAr;
    private final String nameEN;

    FeedbackModel(int id, String nameAr, String nameEN) {

        this.id = id;
        this.nameAr = nameAr;
        this.nameEN = nameEN;
    }

    public int getId() {
        return id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEN() {
        return nameEN;
    }
}
