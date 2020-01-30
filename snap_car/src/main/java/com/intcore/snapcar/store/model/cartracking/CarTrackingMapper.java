package com.intcore.snapcar.store.model.cartracking;

import com.google.android.gms.maps.model.LatLng;
import com.intcore.snapcar.core.scope.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class CarTrackingMapper {

    @Inject
    CarTrackingMapper() {

    }

    public CarTrackingModel toCarTrackingModel(CarTrackingApiResponse response) {
        if (response == null) return null;
        return new CarTrackingModel(response.getId(),
                response.getCarId(),
                response.getOldLat(),
                response.getOldLong(),
                response.getLatitude(),
                response.getLongitude());
    }

    public CarTrackingViewModel toCarTrackingViewModel(CarTrackingModel model) {
        if (model == null) return null;
        return new CarTrackingViewModel(model.getId(),
                model.getCarId(),
                new LatLng(Double.valueOf(model.getOldLat()), Double.valueOf(model.getOldLong())),
                new LatLng(Double.valueOf(model.getLatitude()), Double.valueOf(model.getLongitude())));
    }
}