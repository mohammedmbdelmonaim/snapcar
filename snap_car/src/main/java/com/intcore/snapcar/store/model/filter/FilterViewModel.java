package com.intcore.snapcar.store.model.filter;

import com.intcore.snapcar.store.model.ads.AdsViewModel;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailViewModel;
import com.intcore.snapcar.store.model.hotzone.HotZoneViewModel;

import java.util.Collections;
import java.util.List;

public class FilterViewModel {

    private List<FilterDetailViewModel> maleList;
    private List<FilterDetailViewModel> femaleList;
    private List<FilterDetailViewModel> damageList;
    private List<FilterDetailViewModel> showRoomList;
    private List<FilterDetailViewModel> hotZoneMixList;
    private List<FilterDetailViewModel> hotZoneMaleList;
    private List<FilterDetailViewModel> hotZoneFemaleList;
    private List<CarViewModel> carList;
    private List<AdsViewModel> adsList;
    private List<CarViewModel> featureCarList;
    private List<HotZoneViewModel> hotZoneList;
    private List<DefaultUserModel> showRoomsList;

    FilterViewModel(List<CarViewModel> carList,
                    List<AdsViewModel> adsList,
                    List<CarViewModel> featureCarList,
                    List<HotZoneViewModel> hotZoneList,
                    List<DefaultUserModel> showRoomsList) {
        this.carList = carList;
        this.adsList = adsList;
        this.hotZoneList = hotZoneList;
        this.showRoomsList = showRoomsList;
        this.featureCarList = featureCarList;
    }

    FilterViewModel(List<FilterDetailViewModel> maleList,
                    List<FilterDetailViewModel> femaleList,
                    List<FilterDetailViewModel> damageList,
                    List<FilterDetailViewModel> showRoomList,
                    List<FilterDetailViewModel> hotZoneMixList,
                    List<FilterDetailViewModel> hotZoneMaleList,
                    List<FilterDetailViewModel> hotZoneFemaleList) {
        this.maleList = maleList;
        this.femaleList = femaleList;
        this.damageList = damageList;
        this.showRoomList = showRoomList;
        this.hotZoneMixList = hotZoneMixList;
        this.hotZoneMaleList = hotZoneMaleList;
        this.hotZoneFemaleList = hotZoneFemaleList;
    }

    public static FilterViewModel createDefault() {
        return new FilterViewModel(Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
    }

    public List<CarViewModel> getCarList() {
        return carList;
    }

    public List<AdsViewModel> getAdsList() {
        return adsList;
    }

    public List<CarViewModel> getFeatureCarList() {
        return featureCarList;
    }

    public List<HotZoneViewModel> getHotZoneList() {
        return hotZoneList;
    }

    public List<DefaultUserModel> getShowRoomsList() {
        return showRoomsList;
    }

    public List<FilterDetailViewModel> getMaleList() {
        return maleList;
    }

    public List<FilterDetailViewModel> getFemaleList() {
        return femaleList;
    }

    public List<FilterDetailViewModel> getDamageList() {
        return damageList;
    }

    public List<FilterDetailViewModel> getShowRoomList() {
        return showRoomList;
    }

    public List<FilterDetailViewModel> getHotZoneMixList() {
        return hotZoneMixList;
    }

    public List<FilterDetailViewModel> getHotZoneMaleList() {
        return hotZoneMaleList;
    }

    public List<FilterDetailViewModel> getHotZoneFemaleList() {
        return hotZoneFemaleList;
    }
}