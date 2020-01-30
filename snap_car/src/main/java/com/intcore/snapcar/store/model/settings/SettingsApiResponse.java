package com.intcore.snapcar.store.model.settings;

import com.google.gson.annotations.SerializedName;

public class SettingsApiResponse {

    @SerializedName("commission_percentage")
    private String commissionPercentage;
    @SerializedName("swear_text")
    private String swearTextAr;
    @SerializedName("swear_text_en")
    private String swearTextEn;
    @SerializedName("social_media")
    private String socialMedia;
    @SerializedName("social_media_ar")
    private String socialMediaAr;
    @SerializedName("contact_us")
    private String contactUs;
    @SerializedName("privacy")
    private String privacy;
    @SerializedName("privacy_ar")
    private String privacyAr;
    @SerializedName("address")
    private String address;
    @SerializedName("address_en")
    private String addressEn;
    @SerializedName("mail")
    private String mail;
    @SerializedName("phones")
    private String phones;
    @SerializedName("facebook")
    private String facebook;
    @SerializedName("twitter")
    private String twitter;
    @SerializedName("snapchat")
    private String snapchat;
    @SerializedName("youtube")
    private String youtube;
    @SerializedName("instagram")
    private String instagrem;
    @SerializedName("premium_price")
    private int premiumPrice;

    public int getPremiumPrice() {
        return premiumPrice;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getInstagrem() {
        return instagrem;
    }

    public String getSocialMedia() {
        return socialMedia;
    }

    public String getContactUs() {
        return contactUs;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getCommissionPercentage() {
        return commissionPercentage;
    }

    public String getSocialMediaAr() {
        return socialMediaAr;
    }

    public String getAddress() {
        return address;
    }

    public String getMail() {
        return mail;
    }

    public String getPhones() {
        return phones;
    }

    public String getPrivacyAr() {
        return privacyAr;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getSnapchat() {
        return snapchat;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getSwearTextAr() {
        return swearTextAr;
    }

    public String getSwearTextEn() {
        return swearTextEn;
    }

    public String getAddressEn() {
        return addressEn;
    }
}