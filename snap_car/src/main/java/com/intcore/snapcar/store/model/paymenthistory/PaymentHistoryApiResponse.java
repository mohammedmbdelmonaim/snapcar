package com.intcore.snapcar.store.model.paymenthistory;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.car.CarApiResponse;

public class PaymentHistoryApiResponse {

    @SerializedName("user_id")
    private String userId;
    @SerializedName("car_id")
    private String carId;
    @SerializedName("total_amount")
    private String totalAmount;
    @SerializedName("commission")
    private String commission;
    @SerializedName("paid_at")
    private String paidAt;
    @SerializedName("status")
    private String status;
    @SerializedName("car")
    private CarApiResponse car;
    @SerializedName("ads")
    private Ads ads;

    public Ads getAds() {
        return ads;
    }
    public String getStatus() {
        return status;
    }

    public CarApiResponse getCar() {
        return car;
    }

    public String getUserId() {
        return userId;
    }

    public String getCarId() {
        return carId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getCommission() {
        return commission;
    }

    public String getPaidAt() {
        return paidAt;
    }

    public class Ads{
        @SerializedName("id")
        private int id;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;
        @SerializedName("image")
        private String image;
        @SerializedName("start_date")
        private String startDate;
        @SerializedName("end_date")
        private String endDate;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public String getImage() {
            return image;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }
    }
}
