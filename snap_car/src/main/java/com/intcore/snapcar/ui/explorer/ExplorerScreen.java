package com.intcore.snapcar.ui.explorer;

import android.annotation.SuppressLint;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailViewModel;

public interface ExplorerScreen {

    @SuppressLint("MissingPermission")
    void startReceivingLocationUpdates(LocationCallback locationCallback);

    void stopReceivingLocationUpdates(LocationCallback locationCallback);

    void updateClusterItems(FilterDetailViewModel filterDetailViewModel);

    void setMapIcons(FilterDetailViewModel viewModel);

    void showErrorMessage(String errorMsg);

    void showWarningMessage(String s);

    void showSuccessMessage(String s);

    void showGPSIsRequiredMessage();

    void updateMap(LatLng latLng);

    void showLoadingAnimation();

    void hideLoadingAnimation();

    void setupMap();

    void processLogout();

    void clearMap();

    void shouldNavigateToMap(FilterDetailViewModel filterDetailViewModel);

    void requestLocationPermission();

    void setupCameraChangeListener();

}