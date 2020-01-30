package com.intcore.snapcar.store.model.settings;

import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import javax.inject.Inject;

@ApplicationScope
public class SettingsMapper {

    @Inject
    SettingsMapper() {
    }

    public SettingsModel toModel(SettingsApiResponse settingsApiResponse) {
        if (settingsApiResponse == null) return null;
        String swear;
        String address;
        if (isEnglish()) {
            swear = settingsApiResponse.getSwearTextEn();
            address = settingsApiResponse.getAddressEn();
        } else {
            swear = settingsApiResponse.getSwearTextAr();
            address = settingsApiResponse.getAddress();

        }
        return new SettingsModel(
                settingsApiResponse.getCommissionPercentage(),
                swear,
                settingsApiResponse.getSocialMedia(),
                settingsApiResponse.getContactUs(),
                settingsApiResponse.getPrivacy(),
                settingsApiResponse.getSocialMediaAr(),
                settingsApiResponse.getPrivacyAr(),
                settingsApiResponse.getMail(),
                address,
                settingsApiResponse.getPhones(),
                settingsApiResponse.getFacebook(),
                settingsApiResponse.getTwitter(),
                settingsApiResponse.getSnapchat(),
                settingsApiResponse.getInstagrem(),
                settingsApiResponse.getYoutube(),
                settingsApiResponse.getPremiumPrice());
    }

    public SettingViewModel toViewModel(SettingsApiResponse settingsApiResponse) {
        if (settingsApiResponse == null) return null;
        return new SettingViewModel(
                settingsApiResponse.getCommissionPercentage(),
                settingsApiResponse.getSwearTextEn(),
                settingsApiResponse.getSocialMedia(),
                settingsApiResponse.getContactUs(),
                settingsApiResponse.getPrivacy(), settingsApiResponse.getPremiumPrice());
    }

    private boolean isEnglish() {
        return LocaleUtil.getLanguage(SnapCarApplication.getContext()).equals("en");
    }
}