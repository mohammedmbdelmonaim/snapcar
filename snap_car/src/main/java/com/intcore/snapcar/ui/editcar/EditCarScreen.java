package com.intcore.snapcar.ui.editcar;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface EditCarScreen extends BaseActivityScreen{
    void updateUi(CarDTO carDTO);

    void setNewImagePath(String s);

    void showGPSIsRequiredMessage();

    void startReceivingLocationUpdates(LocationCallback locationCallback);

    void updateLatLong(LatLng latLng);

    void carUpdated();

    void setSwearData(SettingsModel settingsApiResponse);

    void processLogout();

    void setCurrentExaminationPosition(int currentExaminationPosition);

    void finishActivity();
}
