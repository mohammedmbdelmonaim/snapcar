package com.intcore.snapcar.store.model.showroom;

import com.intcore.snapcar.store.model.hotzone.HotZoneMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class ShowRoomInfoMapper {

    private final HotZoneMapper hotZoneMapper;

    @Inject
    ShowRoomInfoMapper(HotZoneMapper hotZoneMapper) {
        this.hotZoneMapper = hotZoneMapper;
    }

    public ShowRoomInfoModel toShowRoomInfoModel(ShowRoomInfoApiResponse response) {
        if (response == null) return null;
        return new ShowRoomInfoModel(response.getOpenTimeTwo(),
                response.getClosedTimeTwo(),
                response.getOpenTime(),
                response.getClosedTime(),
                response.getLongitude(),
                response.getLatitude(),
                response.getUserId(),
                response.getVerifyLatter(),
                response.getPhones(),
                response.getDealingWith(),
                response.getPremium(),
                hotZoneMapper.toHotZoneModel(response.getHotZone()),
                response.phonesPositions);
    }

    public ShowRoomInfoViewModel toShowRoomInfoViewModel(ShowRoomInfoModel model) {
        return new ShowRoomInfoViewModel(model.getOpenTime());
    }
}