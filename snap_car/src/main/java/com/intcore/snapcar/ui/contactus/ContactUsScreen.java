package com.intcore.snapcar.ui.contactus;

import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface ContactUsScreen extends BaseActivityScreen {

    void showMessageErrorMsg(String messageErrorMsg);

    void showEmailErrorMsg(String emailErrorMsg);

    void updateUi(SettingsModel settingsModel);

    void showNameErrorMsg(String nameErrorMsg);

    void onPhoneClicked(String phones);

    void onSaveSuccessfully();

    void setupEditText();
}