package com.intcore.snapcar.ui.mycars;

import com.intcore.snapcar.store.model.mycars.MyCarsApiResponse;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import okhttp3.ResponseBody;

public interface MyCarsScreen extends BaseActivityScreen{
    void updateUi(MyCarsApiResponse myCarsApiResponse);

    void carRemoved(ResponseBody responseBody);

    void openSwearDialog(SettingsModel settingsApiResponse);

    void setCarId(int carId);

    void processLogout();

    void showConfirmationDialog(int carId, int isTraked);
}
