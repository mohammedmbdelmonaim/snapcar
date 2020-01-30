package com.intcore.snapcar.store.model.car;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarDTO {
    @SerializedName("car")
    private CarApiResponse car;
    @SerializedName("cars")
    private List<CarApiResponse> cars;
    @SerializedName("data")
    private CarApiResponse insertedCar;


    public List<CarApiResponse> getCars() {
        return cars;
    }

    public CarApiResponse getCar() {
        return car;
    }

    public CarApiResponse getInsertedCar() {
        return insertedCar;
    }
}
