package com.intcore.snapcar.ui.addcarphotes;

public class Image {

    private String image;
    private int place;
    private int is_main;

    Image(String image, int place, int is_main) {
        this.image = image;
        this.place = place;
        this.is_main = is_main;
    }

    public String getImage() {
        return image;
    }

    public int getPlace() {
        return place;
    }

    public int getIs_main() {
        return is_main;
    }
}
