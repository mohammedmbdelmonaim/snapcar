package com.intcore.snapcar.store.model.settings;

public class SettingsModel {

    private String commissionPercentage;
    private String socialMediaAr;
    private String socialMedia;
    private String contactUs;
    private String swearText;
    private String privacyAr;
    private String facebook;
    private String snapChat;
    private String twitter;
    private String privacy;
    private String address;
    private String phones;
    private String mail;
    private String intagram;
    private String youtube;
    private int premiumPrice;

    SettingsModel(String commissionPercentage,
                  String swearText,
                  String socialMedia,
                  String contactUs,
                  String privacy,
                  String socialMediaAr,
                  String privacyAr,
                  String mail,
                  String address,
                  String phones,
                  String facebook,
                  String twitter,
                  String snapChat,
                  String instgram,
                  String youtube,
                  int premiumPrice) {
        this.commissionPercentage = commissionPercentage;
        this.socialMediaAr = socialMediaAr;
        this.socialMedia = socialMedia;
        this.contactUs = contactUs;
        this.swearText = swearText;
        this.privacyAr = privacyAr;
        this.address = address;
        this.privacy = privacy;
        this.snapChat = snapChat;
        this.twitter = twitter;
        this.facebook = facebook;
        this.phones = phones;
        this.mail = mail;
        this.intagram = instgram;
        this.youtube = youtube;
        this.premiumPrice = premiumPrice;
    }

    public int getPremiumPrice() {
        return premiumPrice;
    }

    public String getIntagram() {
        return intagram;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getCommissionPercentage() {
        return commissionPercentage;
    }

    public String getSocialMediaAr() {
        return socialMediaAr;
    }

    public String getSocialMedia() {
        return socialMedia;
    }

    public String getSwearText() {
        return swearText;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getSnapChat() {
        return snapChat;
    }

    public String getContactUs() {
        return contactUs;
    }

    public String getPrivacyAr() {
        return privacyAr;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getAddress() {
        return address;
    }

    public String getPhones() {
        return phones;
    }

    public String getMail() {
        return mail;
    }
}