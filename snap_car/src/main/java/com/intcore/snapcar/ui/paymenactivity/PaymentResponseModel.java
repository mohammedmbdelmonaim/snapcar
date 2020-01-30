package com.intcore.snapcar.ui.paymenactivity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class PaymentResponseModel {
    @SerializedName("callback_url")
    @Expose
    private String callbackUrl;
    @SerializedName("script_url")
    @Expose
    private String scriptUrl;
    @SerializedName("payment_reference")
    @Expose
    private String paymentReference;
    @SerializedName("code")
    @Expose
    private Integer code;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getScriptUrl() {
        return scriptUrl;
    }

    public void setScriptUrl(String scriptUrl) {
        this.scriptUrl = scriptUrl;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
