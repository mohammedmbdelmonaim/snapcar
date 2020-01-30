package com.intcore.snapcar.store.model.filter;

import com.intcore.snapcar.store.model.ads.AdsMapper;
import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.constant.FilterType;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserMapper;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailMapper;
import com.intcore.snapcar.store.model.hotzone.HotZoneMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.Collections;

import javax.inject.Inject;

@ApplicationScope
public class FilterMapper {

    private final FilterDetailMapper filterDetailMapper;
    private final DefaultUserMapper defaultUserMapper;
    private final HotZoneMapper hotZoneMapper;
    private final AdsMapper adsMapper;
    private final CarMapper carMapper;

    @Inject
    FilterMapper(FilterDetailMapper filterDetailMapper, DefaultUserMapper defaultUserMapper, HotZoneMapper hotZoneMapper, AdsMapper adsMapper, CarMapper carMapper) {
        this.filterDetailMapper = filterDetailMapper;
        this.defaultUserMapper = defaultUserMapper;
        this.hotZoneMapper = hotZoneMapper;
        this.adsMapper = adsMapper;
        this.carMapper = carMapper;
    }

    public FilterViewModel toFilterViewModel(FilterApiResponse response, int counter) {
        switch (counter) {
            case 1:
                return new FilterViewModel(
                        filterDetailMapper.toFilterDetailViewModelsFromCars(
                                carMapper.toCarModels(response.getMaleList()), FilterType.MALE),
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList());
            case 2:
                return new FilterViewModel(Collections.emptyList(),
                        filterDetailMapper.toFilterDetailViewModelsFromCars(
                                carMapper.toCarModels(response.getFemaleList()), FilterType.FEMALE),
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList());
            case 3:
                return new FilterViewModel(Collections.emptyList(), Collections.emptyList(),
                        filterDetailMapper.toFilterDetailViewModelsFromCars(
                                carMapper.toCarModels(response.getDamageList()), FilterType.DAMAGED),
                        Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList());
            case 4:
                return new FilterViewModel(Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        filterDetailMapper.toFilterDetailViewModelsFromShowRooms(
                                defaultUserMapper.toDefaultUserModels(response.getShowRoomList()), FilterType.SHOW_ROOM),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList());
            case 5:
                return new FilterViewModel(Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(),
                        filterDetailMapper.toFilterDetailViewModelsFromHotZones(
                                hotZoneMapper.toHotZoneModels(response.getHotZoneMixList()), FilterType.HOT_ZONE_MIXED),
                        Collections.emptyList(), Collections.emptyList());
            case 6:
                return new FilterViewModel(Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(),
                        filterDetailMapper.toFilterDetailViewModelsFromHotZones(
                                hotZoneMapper.toHotZoneModels(response.getHotZoneFemaleList()), FilterType.HOT_ZONE_FEMALE),
                        Collections.emptyList());
            case 7:
                return new FilterViewModel(Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(),
                        filterDetailMapper.toFilterDetailViewModelsFromHotZones(
                                hotZoneMapper.toHotZoneModels(response.getHotZoneMaleList()), FilterType.HOT_ZONE_MALE));
        }
        return FilterViewModel.createDefault();
    }

    public FilterModel toFilterModel(FilterApiResponse response) {
        return new FilterModel(
                carMapper.toCarModels(response.getCarsList()),
                adsMapper.toAdsModels(response.getAdsList()),
                carMapper.toCarModels(response.getFeatureList()),
                hotZoneMapper.toHotZoneModels(response.getHotZoneList() , defaultUserMapper),
                defaultUserMapper.toDefaultUserModels(response.getShowRoomsList()));
    }

    public FilterViewModel toFilterViewModel(FilterModel model) {
        return new FilterViewModel(
                carMapper.toCarViewModels(model.getCarList()),
                adsMapper.toAdsViewModels(model.getAdsList()),
                carMapper.toCarViewModels(model.getFeatureCarList()),
                hotZoneMapper.toHotZoneViewModels(model.getHotZoneList()),
                model.getShowRoomsList());
    }
}