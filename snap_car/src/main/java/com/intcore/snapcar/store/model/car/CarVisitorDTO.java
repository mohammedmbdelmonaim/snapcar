package com.intcore.snapcar.store.model.car;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarVisitorDTO {

    @SerializedName("cars")
    private List<CarApiResponse> carApiResponses;

    public List<CarApiResponse> getCarApiResponses() {
        return carApiResponses;
    }
}