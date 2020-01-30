package com.intcore.snapcar.store.model.coupon;

import android.os.Parcel;
import android.os.Parcelable;

import com.intcore.snapcar.store.model.hotzone.HotZoneViewModel;

public class CouponViewModel implements Parcelable {

    public static final String TAG = "CoupopnTag";
    public static final Creator<CouponViewModel> CREATOR = new Creator<CouponViewModel>() {
        @Override
        public CouponViewModel createFromParcel(Parcel in) {
            return new CouponViewModel(in);
        }

        @Override
        public CouponViewModel[] newArray(int size) {
            return new CouponViewModel[size];
        }
    };
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
    private HotZoneViewModel hotZoneViewModel;

    CouponViewModel(int id,
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
                    HotZoneViewModel hotZoneViewModel) {
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
        this.hotZoneViewModel = hotZoneViewModel;
    }

    protected CouponViewModel(Parcel in) {
        id = in.readInt();
        uses = in.readInt();
        nameAr = in.readString();
        nameEn = in.readString();
        hotZoneId = in.readInt();
        coupon = in.readString();
        amount = in.readString();
        activation = in.readInt();
        expireAt = in.readString();
        deletedAt = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        descriptionAr = in.readString();
        descriptionEn = in.readString();
        isProvidedByHotZone = in.readInt();
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

    public HotZoneViewModel getHotZoneViewModel() {
        return hotZoneViewModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(uses);
        dest.writeString(nameAr);
        dest.writeString(nameEn);
        dest.writeInt(hotZoneId);
        dest.writeString(coupon);
        dest.writeString(amount);
        dest.writeInt(activation);
        dest.writeString(expireAt);
        dest.writeString(deletedAt);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(descriptionAr);
        dest.writeString(descriptionEn);
        dest.writeInt(isProvidedByHotZone);
    }
}