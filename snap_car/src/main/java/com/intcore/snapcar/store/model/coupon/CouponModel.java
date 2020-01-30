package com.intcore.snapcar.store.model.coupon;

import com.intcore.snapcar.store.model.hotzone.HotZoneModel;

public class CouponModel {

    private int id;
    private int uses;
    private String nameAr;
    private String nameEn;
    private int hotZoneId;
    private String coupon;
    private String amount;
    private int activation;
    private String expireAt;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;
    private String descriptionAr;
    private String descriptionEn;
    private int isProvidedByHotZone;
    private HotZoneModel hotZoneModel;

    CouponModel(int id,
                int uses,
                String nameAr,
                String nameEn,
                int hotZoneId,
                String coupon,
                String amount,
                int activation,
                String expireAt,
                String deletedAt,
                String createdAt,
                String updatedAt,
                String descriptionAr,
                String descriptionEn,
                int isProvidedByHotZone,
                HotZoneModel hotZoneModel) {
        this.id = id;
        this.uses = uses;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
        this.hotZoneId = hotZoneId;
        this.coupon = coupon;
        this.amount = amount;
        this.activation = activation;
        this.expireAt = expireAt;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.descriptionAr = descriptionAr;
        this.descriptionEn = descriptionEn;
        this.isProvidedByHotZone = isProvidedByHotZone;
        this.hotZoneModel = hotZoneModel;
    }

    public int getId() {
        return id;
    }

    public int getUses() {
        return uses;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public int getHotZoneId() {
        return hotZoneId;
    }

    public String getCoupon() {
        return coupon;
    }

    public String getAmount() {
        return amount;
    }

    public int getActivation() {
        return activation;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public int getIsProvidedByHotZone() {
        return isProvidedByHotZone;
    }

    public HotZoneModel getHotZoneModel() {
        return hotZoneModel;
    }
}