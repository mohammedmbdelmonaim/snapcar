package com.intcore.snapcar.ui.host;

import android.location.Location;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;

import javax.inject.Inject;

@ActivityScope
class HostPresenter extends BaseActivityPresenter {

    private final PreferencesUtil preferencesUtil;
    private final ResourcesUtil resourcesUtil;
    private final UserManager userManager;
    private final HostScreen hostScreen;
    private LocationCallback locationCallback;
    private Location lastKnownLocation;

    @Inject
    HostPresenter(ResourcesUtil resourcesUtil,
                  UserManager userManager,
                  PreferencesUtil preferencesUtil,
                  HostScreen hostScreen) {
        super(hostScreen);
        this.locationCallback = createLocationCallback();
        this.preferencesUtil = preferencesUtil;
        this.resourcesUtil = resourcesUtil;
        this.userManager = userManager;
        this.hostScreen = hostScreen;
    }

    @Override
    protected void onCreate() {
        hostScreen.setupTrackedCar();
        showInterestPopup();
    }

    private void showInterestPopup() {
        if (userManager.sessionManager().isSessionActive()) {
            preferencesUtil.saveOrUpdateInt("interest_count", preferencesUtil.getInt("interest_count", 0) + 1);
            if (preferencesUtil.getInt("interest_count", 0) == 3) {
                hostScreen.showInterestPopup();
            }
        }
    }


    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location result = locationResult.getLastLocation();
                if (result != null) {
                    lastKnownLocation = result;
                }
            }
        };
    }

    Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    @Override
    protected void onStart() {
        startReceivingLocationUpdates();
    }

    @Override
    protected void onStop() {
        stopReceivingLocationUpdates();
    }

    private void startReceivingLocationUpdates() {
        if (getPermissionUtil().hasLocationPermission()) {
            if (getPermissionUtil().isGPSEnabled()) {
                hostScreen.startReceivingLocationUpdates(locationCallback);
            } else {
                hostScreen.showGPSIsRequiredMessage();
            }
        } else {
            getPermissionUtil().requestLocationPermission();
        }
    }

    private void stopReceivingLocationUpdates() {
        hostScreen.stopReceivingLocationUpdates(locationCallback);
    }

    boolean isUserHaveTrackedCar() {
        if (userManager.sessionManager().isSessionActive()) {
            if (userManager.getCurrentUser().getCarModel() == null) {
                return false;
            } else return true;
        } else {
            return false;
        }
    }
}