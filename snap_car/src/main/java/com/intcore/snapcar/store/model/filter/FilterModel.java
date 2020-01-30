package com.intcore.snapcar.store.model.filter;

import com.intcore.snapcar.store.model.ads.AdsModel;
import com.intcore.snapcar.store.model.car.CarModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailModel;
import com.intcore.snapcar.store.model.hotzone.HotZoneModel;

import java.util.List;

public class FilterModel {

    private List<FilterDetailModel> maleList;
    private List<FilterDetailModel> femaleList;
    private List<FilterDetailModel> showRoomList;
    private List<FilterDetailModel> hotZoneMixList;
    private List<FilterDetailModel> hotZoneMaleList;
    private List<FilterDetailModel> hotZoneFemaleList;
    private List<CarModel> carList;
    private List<AdsModel> adsList;
    private List<CarModel> featureCarList;
    private List<HotZoneModel> hotZoneList;
    private List<DefaultUserModel> showRoomsList;

    FilterModel(List<CarModel> carList,
                List<AdsModel> adsList,
                List<CarModel> featureCarList,
                List<HotZoneModel> hotZoneList,
                List<DefaultUserModel> showRoomsList) {
        this.carList = carList;
        this.adsList = adsList;
        this.hotZoneList = hotZoneList;
        this.showRoomsList = showRoomsList;
        this.featureCarList = featureCarList;
    }
    FilterModel(List<FilterDetailModel> maleList,
                List<FilterDetailModel> femaleList,
                List<FilterDetailModel> showRoomList,
                List<FilterDetailModel> hotZoneMixList,
                List<FilterDetailModel> hotZoneMaleList,
                List<FilterDetailModel> hotZoneFemaleList) {
        this.maleList = maleList;
        this.femaleList = femaleList;
        this.showRoomList = showRoomList;
        this.hotZoneMixList = hotZoneMixList;
        this.hotZoneMaleList = hotZoneMaleList;
        this.hotZoneFemaleList = hotZoneFemaleList;
    }

    public List<CarModel> getCarList() {
        return carList;
    }

    public List<AdsModel> getAdsList() {
        return adsList;
    }

    public List<CarModel> getFeatureCarList() {
        return featureCarList;
    }

    public List<HotZoneModel> getHotZoneList() {
        return hotZoneList;
    }

    public List<DefaultUserModel> getShowRoomsList() {
        return showRoomsList;
    }

    public List<FilterDetailModel> getMaleList() {
        return maleList;
    }

    public List<FilterDetailModel> getFemaleList() {
        return femaleList;
    }

    public List<FilterDetailModel> getShowRoomList() {
        return showRoomList;
    }

    public List<FilterDetailModel> getHotZoneMixList() {
        return hotZoneMixList;
    }

    public List<FilterDetailModel> getHotZoneMaleList() {
        return hotZoneMaleList;
    }

    public List<FilterDetailModel> getHotZoneFemaleList() {
        return hotZoneFemaleList;
    }
}