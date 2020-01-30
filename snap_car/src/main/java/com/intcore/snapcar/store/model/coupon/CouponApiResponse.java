package com.intcore.snapcar.store.model.coupon;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.hotzone.HotZone;

public class CouponApiResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("name_ar")
    private String nameAr;
    @SerializedName("name_en")
    private String nameEn;
    @SerializedName("description_ar")
    private String descriptionAr;
    @SerializedName("description_en")
    private String descriptionEn;
    @SerializedName("hot_zone_id")
    private int hotZoneId;
    @SerializedName("expire_at")
    private String expireAt;
    @SerializedName("coupon")
    private String coupon;
    @SerializedName("amount")
    private String amount;
    @SerializedName("is_provided_by_hotzone")
    private int isProvidedByHotZone;
    @SerializedName("activation")
    private int activation;
    @SerializedName("uses")
    private int uses;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("hotzone")
    private HotZone hotZone;

    public int getId() {
        return id;
    }

    public String getNameAr() {
        return nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public int getHotZoneId() {
        return hotZoneId;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public String getCoupon() {
        return coupon;
    }

    public String getAmount() {
        return amount;
    }

    public int getIsProvidedByHotZone() {
        return isProvidedByHotZone;
    }

    public int getActivation() {
        return activation;
    }

    public int getUses() {
        return uses;
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

    public HotZone getHotZone() {
        return hotZone;
    }
}