package com.intcore.snapcar.ui.paymenactivity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentCallbackResponseModel {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("car_id")
        @Expose
        private Integer carId;
        @SerializedName("ads_id")
        @Expose
        private Object adsId;
        @SerializedName("total_amount")
        @Expose
        private String totalAmount;
        @SerializedName("commission")
        @Expose
        private String commission;
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("paid_at")
        @Expose
        private Object paidAt;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("payment_reference")
        @Expose
        private String paymentReference;
        @SerializedName("payment_status")
        @Expose
        private String paymentStatus;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getCarId() {
            return carId;
        }

        public void setCarId(Integer carId) {
            this.carId = carId;
        }

        public Object getAdsId() {
            return adsId;
        }

        public void setAdsId(Object adsId) {
            this.adsId = adsId;
        }

        public String getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Object getPaidAt() {
            return paidAt;
        }

        public void setPaidAt(Object paidAt) {
            this.paidAt = paidAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getPaymentReference() {
            return paymentReference;
        }

        public void setPaymentReference(String paymentReference) {
            this.paymentReference = paymentReference;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

    }
}
