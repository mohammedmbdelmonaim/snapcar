package com.intcore.snapcar.ui.privacyactivtiy;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface PrivacyScreen extends BaseActivityScreen{
    void policyText(SettingsModel settingsModel);

    void finishActivity(DefaultUserModel defaultUserModel);

    void logoutReady();
}
