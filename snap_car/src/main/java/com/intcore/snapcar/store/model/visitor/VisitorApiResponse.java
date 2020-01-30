package com.intcore.snapcar.store.model.visitor;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;

import java.util.List;

public class VisitorApiResponse {

    @SerializedName("user")
    private DefaultUserDataApiResponse.DefaultUserApiResponse userApiResponse;
    @SerializedName("cars")
    private List<CarApiResponse> carApiResponses;
    @SerializedName("sold_cars")
    private List<CarApiResponse> soldCarApiResponse;

    public DefaultUserDataApiResponse.DefaultUserApiResponse getUserApiResponse() {
        return userApiResponse;
    }

    public List<CarApiResponse> getSoldCarApiResponse() {
        return soldCarApiResponse;
    }

    public List<CarApiResponse> getCarApiResponses() {
        return carApiResponses;
    }
}
