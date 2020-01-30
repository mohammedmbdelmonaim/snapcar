package com.intcore.snapcar.store.model.filterdetail;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class FilterDetailViewModel implements ClusterItem {

    private int id;
    private int type;
    private int carId;
    private String name;
    private Bitmap icon;
    private String carNumbers;
    private String imageUrl;
    private String year;
    private LatLng newLatLng;
    private LatLng oldLatLng;
    private Boolean isPremium;
    private Boolean isHotZone;
    private Boolean isFavorite;
    private Boolean canProvideDiscount;
    private Boolean isShowroom;
    private BitmapDescriptor bitmapDescriptor;

    public FilterDetailViewModel(int id,
                          int type,
                          int carId,
                          String name,
                          Bitmap icon,
                          String carNumbers,
                          LatLng newLatLng,
                          LatLng oldLatLng,
                          Boolean isPremium,
                          Boolean isHotZone,
                          Boolean isFavorite,
                          String imageUrl,
                          Boolean canProvideDiscount, String year, BitmapDescriptor bitmapDescriptor,boolean isShowroom) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.icon = icon;
        this.carId = carId;
        this.imageUrl = imageUrl;
        this.newLatLng = newLatLng;
        this.oldLatLng = oldLatLng;
        this.isPremium = isPremium;
        this.isHotZone = isHotZone;
        this.isFavorite = isFavorite;
        this.carNumbers = carNumbers;
        this.year = year;
        this.canProvideDiscount = canProvideDiscount;
        this.bitmapDescriptor = bitmapDescriptor;
        this.isShowroom = isShowroom;
    }

    public BitmapDescriptor getBitmapDescriptor() {
        return bitmapDescriptor;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Bitmap
    getIcon() {
        return icon;
    }

    public Boolean getShowroom() {
        return isShowroom;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCarNumbers() {
        return carNumbers;
    }

    public int getCarId() {
        return carId;
    }

    public LatLng getNewLatLng() {
        return newLatLng;
    }

    public void setNewLatLng(LatLng newLatLng) {
        this.newLatLng = newLatLng;
    }

    public LatLng getOldLatLng() {
        return oldLatLng;
    }

    public void setOldLatLng(LatLng oldLatLng) {
        this.oldLatLng = oldLatLng;
    }

    public void setIcon(Bitmap bitmap) {
        this.icon = bitmap;
    }

    public Boolean isPremium() {
        return isPremium;
    }

    public Boolean isHotZone() {
        return isHotZone;
    }

    public Boolean isFavorite() {
        return isFavorite;
    }

    public Boolean canProvideDiscount() {
        return canProvideDiscount;
    }

    @Override
    public LatLng getPosition() {
        return newLatLng;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return null;
    }

}