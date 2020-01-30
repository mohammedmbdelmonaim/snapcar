package com.intcore.snapcar.store.model.interest;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.car.CarApiResponse;

import java.util.List;

public class MyInterestDTO {

    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("last_page")
    private int lastPage;
    @SerializedName("data")
    private List<CarApiResponse> dataList;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public List<CarApiResponse> getDataList() {
        return dataList;
    }

}
