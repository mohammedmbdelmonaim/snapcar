package com.intcore.snapcar.store.model.explore;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.hotzone.HotZoneViewModel;

public class HotZoneExploreItem extends ExploreItem {

    private final HotZoneViewModel hotZoneViewModel;
    private final DefaultUserModel showRoomModel;

    HotZoneExploreItem(HotZoneViewModel hotZoneViewModel) {
        this.hotZoneViewModel = hotZoneViewModel;
        this.showRoomModel = hotZoneViewModel.getShowRoomModel() ;
    }

    public HotZoneViewModel getHotZoneViewModel() {
        return hotZoneViewModel;

    }
    public DefaultUserModel getShowRoomModel() {
        return showRoomModel;

    }

    @Override
    public int getType() {
        return ExploreItemType.HOT_ZONE;
    }
}