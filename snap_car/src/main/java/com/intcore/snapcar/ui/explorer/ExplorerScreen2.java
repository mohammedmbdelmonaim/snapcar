package com.intcore.snapcar.ui.explorer;

import com.google.android.gms.maps.model.LatLng;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.carTrack.TrackModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailViewModel;
import com.intcore.snapcar.store.model.hotzone.HotZone;

import java.util.List;

public interface ExplorerScreen2 {

    void showLoadingAnimation();
    void hideLoadingAnimation();
    void showSuccessMessage(String msg);
    void showErrorMessage(String msg);
    void processLogout();
    void shouldNavigateToMap(LatLng location);

    void onReceiveHotZones(List<HotZone> list);
    void onReceiveCars(List<CarApiResponse> list);
    void onReceiveShowRoom(List<DefaultUserDataApiResponse.DefaultUserApiResponse> list);
    void onCarLocationUpdate(TrackModel trackModel);

}
