package com.intcore.snapcar.ui.host;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationCallback;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface HostScreen extends BaseActivityScreen {

    void setFragment(Fragment fragment, String tag);

    void setHighlightedItem(int id);

    void setupTrackedCar();

    void showGPSIsRequiredMessage();

    void startReceivingLocationUpdates(LocationCallback locationCallback);

    void stopReceivingLocationUpdates(LocationCallback locationCallback);

    void processLogout();

    void showInterestPopup();
}