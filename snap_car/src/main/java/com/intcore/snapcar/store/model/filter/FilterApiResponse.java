package com.intcore.snapcar.store.model.filter;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.ads.AdsApiResponse;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.hotzone.HotZone;

import java.util.List;

public class FilterApiResponse {

    @SerializedName("fetured")
    private List<CarApiResponse> featureList;

    @SerializedName("males")
    private List<CarApiResponse> maleList;

    @SerializedName("females")
    private List<CarApiResponse> femaleList;

    @SerializedName("damaged")
    private List<CarApiResponse> damageList;

    @SerializedName("showroom")
    private List<DefaultUserDataApiResponse.DefaultUserApiResponse> showRoomList;

    @SerializedName("hotzone_females")
    private List<HotZone> hotZoneFemaleList;

    @SerializedName("hotzone_males")
    private List<HotZone> hotZoneMaleList;

    @SerializedName("hotzone_mix")
    private List<HotZone> hotZoneMixList;

    @SerializedName("hotzones")
    private List<HotZone> hotZoneList;

    @SerializedName("showrooms")
    private List<DefaultUserDataApiResponse.DefaultUserApiResponse> showRoomsList;

    @SerializedName("cars")
    private List<CarApiResponse> carsList;

    @SerializedName("ads")
    private List<AdsApiResponse> adsList;

    public List<CarApiResponse> getCarsList() {
        return carsList;
    }

    public List<CarApiResponse> getFeatureList() {
        return featureList;
    }

    public List<AdsApiResponse> getAdsList() {
        return adsList;
    }

    public List<HotZone> getHotZoneList() {
        return hotZoneList;
    }

    public List<DefaultUserDataApiResponse.DefaultUserApiResponse> getShowRoomsList() {
        return showRoomsList;
    }

    public List<CarApiResponse> getMaleList() {
        return maleList;
    }

    public List<CarApiResponse> getFemaleList() {
        return femaleList;
    }

    public List<CarApiResponse> getDamageList() {
        return damageList;
    }

    public List<HotZone> getHotZoneMixList() {
        return hotZoneMixList;
    }

    public List<HotZone> getHotZoneMaleList() {
        return hotZoneMaleList;
    }

    public List<HotZone> getHotZoneFemaleList() {
        return hotZoneFemaleList;
    }

    public List<DefaultUserDataApiResponse.DefaultUserApiResponse> getShowRoomList() {
        return showRoomList;
    }
}