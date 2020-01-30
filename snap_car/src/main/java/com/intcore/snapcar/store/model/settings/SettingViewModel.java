package com.intcore.snapcar.store.model.settings;

public class SettingViewModel {

    private String commissionPercentage;
    private String swearText;
    private String socialMedia;
    private String contactUs;
    private String privacy;
    private int premiumPrice;

    SettingViewModel(String commissionPercentage, String swearText, String socialMedia, String contactUs,
                     String privacy,int premiumPrice) {
        this.commissionPercentage = commissionPercentage;
        this.swearText = swearText;
        this.socialMedia = socialMedia;
        this.contactUs = contactUs;
        this.privacy = privacy;
        this.premiumPrice = premiumPrice;
    }

    public int getPremiumPrice() {
        return premiumPrice;
    }

    public String getCommissionPercentage() {
        return commissionPercentage;
    }

    public String getSwearText() {
        return swearText;
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
}
