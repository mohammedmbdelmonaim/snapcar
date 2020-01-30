package com.intcore.snapcar.ui.settings;

import com.intcore.snapcar.core.base.BaseActivityScreen;

import okhttp3.ResponseBody;

public interface SettingsScreen extends BaseActivityScreen {

    void changeLanguage(ResponseBody responseBody);
}
