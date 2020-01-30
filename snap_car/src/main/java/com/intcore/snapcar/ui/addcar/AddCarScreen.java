package com.intcore.snapcar.ui.addcar;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import okhttp3.ResponseBody;

public interface AddCarScreen extends BaseActivityScreen {

    void startReceivingLocationUpdates(LocationCallback locationCallback);

    void showGPSIsRequiredMessage();

    void updateLatLong(LatLng latLng);

    void carAdded(CarDTO carResourcesApiResponse);

    void setNewImagePath(String s);

    void setSwearData(SettingsModel settingsApiResponse);

    void requestVipSent(ResponseBody responseBody);

    void finishActivity();

    void updatedUser(DefaultUserModel defaultUserModel);

    void processLogout();

    void initEditTexts();

    void navigateToLogin();

    void isSuspend(DefaultUserModel defaultUserModel);

    void stopReceivingLocationUpdates(LocationCallback locationCallback);

    void setCurrentExaminationPosition(int currentExaminationPosition);
}
