package com.intcore.snapcar.store.model.car;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarSearchDTO {

    @SerializedName("cars")
    private Cars cars;

    @SerializedName("fetured")
    private List<CarApiResponse> featuredCars;

    public List<CarApiResponse> getFeaturedCars() {
        return featuredCars;
    }

    public Cars getCars() {
        return cars;
    }

    public static class Cars {

        @SerializedName("data")
        private List<CarApiResponse> carApiResponses;
        @SerializedName("current_page")
        private int currentPage;
        @SerializedName("last_page")
        private int lastPage;

        public int getLastPage() {
            return lastPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public List<CarApiResponse> getCarApiResponses() {
            return carApiResponses;
        }

    }
}
